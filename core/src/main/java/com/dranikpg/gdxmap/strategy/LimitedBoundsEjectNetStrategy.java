package com.dranikpg.gdxmap.strategy;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.dranikpg.gdxmap.GdxMapCodes;
import com.dranikpg.gdxmap.MapHolder;
import com.dranikpg.gdxmap.abstr.FetchType;
import com.dranikpg.gdxmap.abstr.InternetStrategy;
import com.dranikpg.gdxmap.abstr.TileHandlerConnection;
import com.dranikpg.gdxmap.macro.MapTile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

public class LimitedBoundsEjectNetStrategy implements InternetStrategy, Disposable {

    Thread[] ts;
    TileHandlerConnection cn;
    BlockingDeque<MapTile> tq;

    volatile boolean valid = true;

    public int tileTolerance = 1;
    public int levelTolerance = 2;

    public LimitedBoundsEjectNetStrategy(int maxCN) {
        ts = new Thread[maxCN];
        tq = new LinkedBlockingDeque<MapTile>();
        for(int i = 0;i < maxCN; i++){
            Thread t = new Thread(new RequestHandler());
            t.setDaemon(true);
            t.start();
            t.setName("" + i);
            ts[i] = t;
        }
    }

    @Override
    public void request(MapTile t, TileHandlerConnection cn) {
        if(this.cn ==null)this.cn = cn;
        tq.add(t);
    }

    @Override
    public void dispose() {
        valid = false;
    }

    class RequestHandler implements Runnable{
        @Override
        public void run() {
            while (valid){
                final MapTile t;
                try {
                    t = tq.takeLast();
                }catch (Exception e){
                    System.err.print(e);
                    continue;
                }

                boolean r = t.loadp >= GdxMapCodes.FORCED_LOAD;
                if(!r) {
                    for (MapHolder hd : cn.THgetHolders()) {
                        if (hd.inBoundsWithTolerance(t.x, t.y, t.level, tileTolerance, levelTolerance)) {
                            r = true;
                            break;
                        }
                    }
                }
                //eject if tile is not visible or small load priority
                if(!r){
                    t.cancelLoad();
                    continue;
                }
                try{
                    String url = cn.THgetProviderInfo().getUrl(t);
                    final byte[] fetched = bytesFromURL(url);
                    cn.THgetExecution().openGLRun(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        Pixmap p = new Pixmap(fetched, 0, fetched.length);
                                        //
                                        t.provideTexture(new Texture(p));
                                        //
                                        cn.loaded(fetched, t, FetchType.INTERNET);
                                        //
                                        p.dispose();
                                    }catch (Exception e){

                                    }
                                }
                            }
                    );

                }catch (Exception e){
                    t.cancelLoad();
                    continue;
                }

            }
        }
    }


    private static byte[] bytesFromURL(String urlstr) throws Exception{
        URL url = new URL(urlstr);

        URLConnection c = url.openConnection();
        c.setConnectTimeout(2000);
        c.connect();

        InputStream in = new BufferedInputStream(c.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }
}
