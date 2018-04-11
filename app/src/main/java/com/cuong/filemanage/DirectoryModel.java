package com.cuong.filemanage;

/**
 * Created by Cuong on 3/31/2018.
 */

public class DirectoryModel {
    int type ;
    String name;
    String path;
    String size;


    public DirectoryModel(String name, String path, String size) {

        this.name = name;
        this.path = path;
        this.size = size;

    }



    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSize() {
        return size;
    }

    }



