package com.dranikpg.gdxmap.strategy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.dranikpg.gdxmap.abstr.FetchType;
import com.dranikpg.gdxmap.abstr.InternetStrategy;
import com.dranikpg.gdxmap.abstr.TileHandlerConnection;
import com.dranikpg.gdxmap.macro.MapTile;

public class StupidGdxNetStrategy implements InternetStrategy {
    @Override
    public void request(final MapTile t, final TileHandlerConnection cn) {
        Net.HttpRequest r = new Net.HttpRequest(Net.HttpMethods.GET);
        r.setUrl(cn.THgetProviderInfo().getUrl(t));

        Gdx.net.sendHttpRequest(r, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                final byte[] ct = httpResponse.getResult();
                cn.THgetExecution().openGLRun(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Pixmap p = new Pixmap(ct, 0, ct.length);
                            //
                            t.provideTexture(new Texture(p));
                            //
                            cn.loaded(ct, t, FetchType.INTERNET);
                            //
                            p.dispose();
                        }catch (Exception e){
                            Gdx.app.error("GdxMaps","",e);
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                System.out.println(t.toString());
            }

            @Override
            public void cancelled() {

            }
        });
    }
}
