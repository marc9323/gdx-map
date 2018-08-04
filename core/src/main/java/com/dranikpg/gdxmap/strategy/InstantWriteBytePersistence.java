package com.dranikpg.gdxmap.strategy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.dranikpg.gdxmap.abstr.FetchType;
import com.dranikpg.gdxmap.abstr.PersistenceStrategy;
import com.dranikpg.gdxmap.abstr.TileHandlerConnection;
import com.dranikpg.gdxmap.impl.FileByteCache;
import com.dranikpg.gdxmap.macro.MapTile;

public class InstantWriteBytePersistence implements PersistenceStrategy {

    FileByteCache bc;

    public InstantWriteBytePersistence(FileHandle hd) {
        if(!hd.exists())hd.mkdirs();
         bc = new FileByteCache(hd);
    }

    @Override
    public void read(final MapTile t, final TileHandlerConnection cn) {
        cn.THgetExecution().ioRun(new Runnable() {
            @Override
            public void run() {
                final byte[] ar = bc.read(t.code);
                final Pixmap p = new Pixmap(ar, 0, ar.length);
                cn.THgetExecution().openGLRun(new Runnable() {
                    @Override
                    public void run() {
                        t.provideTexture(new Texture(p));
                        p.dispose();
                        cn.loaded(ar, t, FetchType.STORAGE);
                    }
                });

            }
        });
    }

    @Override
    public boolean has(long code) {
        return bc.has(code);
    }

    @Override
    public void fetched(final byte[] ar, final long code, TileHandlerConnection cn) {
       cn.THgetExecution().ioRun(new Runnable() {
            @Override
            public void run() {
                bc.write(ar, code, true);
            }
        });
    }


}
