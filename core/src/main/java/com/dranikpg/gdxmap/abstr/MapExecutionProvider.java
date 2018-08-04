package com.dranikpg.gdxmap.abstr;

public interface MapExecutionProvider {

    void ioRun(Runnable r);

    void genericRun(Runnable r);

    void openGLRun(Runnable r);

}
