package com.dranikpg.gdxmap.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileByteCache {

    FileHandle local;

    public FileByteCache(FileHandle hd){
        local = hd;
    }


    public void write(byte[] ar, long code, boolean overwrite){
        FileHandle hd = local.child(Long.toString(code));
        if(hd.exists() && !overwrite)return;
        hd.writeBytes(ar,false);
    }

    public byte[] read(long code){
        FileHandle hd = local.child(Long.toString(code));
        if(hd.exists())return hd.readBytes();
        else return null;
    }

    public boolean has(long code){
        return local.child(Long.toString(code)).exists();
    }

    public int entries(){
        String[] names = local.file().list();
        if(names == null) return 0;
        return names.length;
    }

    public long[] list(){
        String[] fnames = local.file().list();
        if(fnames == null || fnames.length == 0)return new long[0];
        long[] ar = new long[fnames.length];
        for(int i = 0; i < fnames.length; i++){
            try{
                long code = Long.parseLong(fnames[i]);
                ar[i] = code;
            }catch (Exception e){

            }
        }
        return ar;
    }

    public void clear(){
        local.emptyDirectory();
    }

    public void delete(long code){
        FileHandle hd = local.child(Long.toString(code));
        if(hd.exists())hd.delete();
    }


}
