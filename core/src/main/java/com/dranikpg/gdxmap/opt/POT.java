package com.dranikpg.gdxmap.opt;

public class POT {

    public static float of(int p){
        if(p < 0)return (float) Math.pow(2,p);
        return 1<<p;
    }

}
