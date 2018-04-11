package com.cuong.filemanage;

/**
 * Created by Cuong on 3/29/2018.
 */

public class FileModel {
    int type ;
    String name;
    String path;
    String time;
    String extra;

    public FileModel(int type, String name, String path, String time, String extra) {
        this.type = type;
        this.name = name;
        this.path = path;
        this.time = time;
        this.extra = extra;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getTime() {
        return time;
    }

    public String getExtra() {
        return extra;
    }
}
