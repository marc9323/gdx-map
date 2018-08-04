package com.dranikpg.gdxmap;

import com.badlogic.gdx.math.MathUtils;
import com.dranikpg.gdxmap.macro.MapTile;

public class GdxMapCodes {
    public static int MAX_COORD = (int) 1e7;
    public static int MAX_LEVEL = (int) 1e2;

    public static final int FETCH_NONE = 0;
    public static final int FETCH_FILEIO = 1;
    public static final int FETCH_INTERNET = 2;

    public static final int VIEW_LOAD = 5;
    public static final int FORCED_LOAD = 10;

    private static int BITS_IN_COORD;
    private static int BITS_IN_LEVEL;
    private static long X_MASK;
    private static long Y_MASK;
    private static long Z_MASK;

    public static void generate(){
        BITS_IN_COORD = MathUtils.ceil(MathUtils.log2(MAX_COORD));
        BITS_IN_LEVEL = MathUtils.ceil(MathUtils.log2(MAX_LEVEL));

//        Gdx.app.debug("GdxMaps","Long bits used " + BITS_IN_COORD*2+BITS_IN_LEVEL);

        for(int x = 0; x < BITS_IN_COORD; x++){
            X_MASK |= 1L << x;
        }

        for(int x = BITS_IN_COORD; x < 2*BITS_IN_COORD; x++){
            Y_MASK |= 1L << x;
        }

        for(int x = BITS_IN_COORD*2; x < 2*BITS_IN_COORD+BITS_IN_LEVEL; x++){
            Z_MASK |= 1L << x;
        }
    }


    public static long code(MapTile t){
        long l = t.x;
        l |= ((long)t.y) << BITS_IN_COORD;
        l |= ((long) t.level) << BITS_IN_COORD*2;
        return l;
    }

    public static long code(int level, int x, int y){
        long l = x;
        l |= ((long)y) << BITS_IN_COORD;
        l |= ((long) level) << BITS_IN_COORD*2;
        return l;
    }

    public static int level(long l){
        l &= Z_MASK;
        l = l >> BITS_IN_COORD*2;
        return (int) l;
    }

    public static int x(long l){
        l &= X_MASK;
        return (int) l;
    }

    public static int y(long l){
        l &= Y_MASK;
        l = l >> BITS_IN_COORD;
        return (int) l;
    }

    public static void set(MapTile t, long l){
        t.x = x(l);
        t.y = y(l);
        t.level = level(l);
    }




}
