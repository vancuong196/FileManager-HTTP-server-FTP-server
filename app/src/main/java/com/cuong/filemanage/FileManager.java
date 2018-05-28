package com.cuong.filemanage;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cuong on 3/29/2018.
 */

public class FileManager {
    public FileManager() {

    }

    static ArrayList<FileModel> getFileList(String path) {
        File directory = new File(path);
        ArrayList<FileModel> fileList = new ArrayList<>();
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);

        for (int i = 0; i < files.length; i++) {
            if (!files[i].canRead() || !files[i].canWrite()) {
                continue;
            }
            String fname = files[i].getName();
            String fpath = files[i].getPath();
            SimpleDateFormat fmtDate = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);
            SimpleDateFormat fmtPast = new SimpleDateFormat("MMM dd  yyyy", Locale.ENGLISH);
            Date d = new Date(files[i].lastModified());
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            cal.setTimeInMillis(files[i].lastModified());
            String ftime;
            if( cal.get(Calendar.YEAR)==currentYear )
            {
                ftime = fmtDate.format(cal.getTime());
            }
            else
            {
                ftime = fmtPast.format(cal.getTime());
            }
            String extra;
            int type;
            boolean isfolder = files[i].isDirectory();

            if (isfolder) {
                extra= String.valueOf(files[i].listFiles().length)+" items";
                type =  Constants.FOLDER_TYPE;
            }
            else {
                if (files[i].getName().toLowerCase().endsWith(".mp3") || files[i].getName().toLowerCase().endsWith(".flat") || files[i].getName().endsWith(".aac")) {
                    type = Constants.MUSIC_FILE_TYPE;
                } else if (files[i].getName().toLowerCase().endsWith(".mp4") || files[i].getName().toLowerCase().endsWith(".3gp")) {
                    type = Constants.VIDEO_FILE_TYPE;
                } else if (files[i].getName().toLowerCase().endsWith(".doc") || files[i].getName().toLowerCase().endsWith(".docx") || files[i].getName().endsWith(".pdf")) {
                    type = Constants.DOCUMENT_FILE_TYPE;
                } else if (files[i].getName().toLowerCase().endsWith(".png") || files[i].getName().toLowerCase().endsWith(".jpg")) {
                    type = Constants.PHOTO_FILE_TYPE;
                } else if (files[i].getName().toLowerCase().endsWith(".zip") || files[i].getName().toLowerCase().endsWith(".rar")) {
                    type = Constants.COMPRESSED_FILE_TYPE;
                } else if (files[i].getName().toLowerCase().endsWith(".apk")) {
                    type = Constants.APP_FILE_TYPE;
                } else {
                    type = Constants.OTHER_FILE_TYPE;
                }
                extra = Utils.size(files[i].length());
            }
            fileList.add(new FileModel(type,fname,fpath,ftime,extra));

        }

        return fileList ;
    }

    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {
        File target = new File(targetLocation.getPath() + "/" + sourceLocation.getName());
        if (sourceLocation.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), target);
            }
        } else {

            copyFile(sourceLocation, target);
        }
    }


    public static void copyFile(File sourceLocation, File targetLocation)
            throws IOException {
        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        }

    public static boolean deleteDirectory(File path) {
        if (path.exists() && path.isFile()) {
            return path.delete();
        }
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }


}
