package com.cuong.filemanage.HttpServer;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Cuong on 3/19/2018.
 */

public class HtmlHelper {
    String mRoutePath;

    public HtmlHelper(String routePath) {
        mRoutePath = routePath;
    }
    public String getByteArray(){
        StringBuilder stringBuilder =new StringBuilder();
        stringBuilder.append("<html>"+'\n'+"<body>"+'\n');
        stringBuilder.append("<form action=\"\" method=\"post\" enctype=\"multipart/form-data\">");
        stringBuilder.append("Select image to upload:");
        stringBuilder.append("<input type=\"file\" name=\"fileToUpload\" id=\"fileToUpload\">");
        stringBuilder.append("<input type=\"submit\" value=\"Upload\" name=\"submit\"></form>");
        stringBuilder.append("<br>");
        String path;
        if (mRoutePath.isEmpty()) {
           // path = Environment.getExternalStorageDirectory().toString();
            path = "/storage/emulated/0";
        } else
        if (mRoutePath.equals("favicon.ico")){
            path = Environment.getExternalStorageDirectory().toString();
        }
        else {
            path = "/"+mRoutePath;
        }

        Log.d("Files", "Path: " + path);

        File directory = new File(path);

        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].canRead()) {
                String tmp = files[i].getName();
                String tmp2 = files[i].getPath();
                stringBuilder.append("<a href=\"" + tmp2 + "\">" + tmp + "</a>" + '\n');
                stringBuilder.append("<br>" + '\n');
            }
        }
        stringBuilder.append("</body>"+'\n'+"</html>"+'\n');
        return stringBuilder.toString();
    }
}
