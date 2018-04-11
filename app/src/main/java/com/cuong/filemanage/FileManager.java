package com.cuong.filemanage;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.sql.DriverManager.println;

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
                if (files[i].getName().endsWith(".mp3")) {
                    type = Constants.MUSIC_FILE_TYPE;
                }
                type = Constants.OTHER_FILE_TYPE;
                extra = Utils.size(files[i].length());
            }
            fileList.add(new FileModel(type,fname,fpath,ftime,extra));

        }

        return fileList ;
    }
    public boolean makeFolder(String path) throws IOException {
        File f = null;
        if( f.exists() )
        {
            throw new IOException("Folder exist!");
        }

        if( f.mkdir() )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean deleteFile(String path) throws IOException {
        File f = null;
        f = new File(path);

        if( !f.exists() )
        {
           throw new IOException("File not found!");

        }

        if( (f.isFile()||f.isDirectory()) && f.delete() )
        {
            return true;
        }
        else
        {
            throw new IOException("Error while deleting file!");

        }
    }

}
