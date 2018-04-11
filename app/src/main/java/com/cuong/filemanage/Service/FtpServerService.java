package com.cuong.filemanage.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.cuong.filemanage.FtpServer.FTPServer;
import com.cuong.filemanage.Ultility.Constant;

import java.io.IOException;

public class FtpServerService extends Service {
    private static FTPServer sServer;
    private static int sPort;
    private static String sPath;
    public FtpServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == Constant.ACTION_START_SERVICE) {
            sPort = intent.getIntExtra(Constant.EXTRA_PORT,2121);
            sPath = intent.getStringExtra(Constant.EXTRA_PATH);

            if (sServer==null){
                try {
                    sServer = new FTPServer(2121);
                    sServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else
        if (action == Constant.ACTION_STOP_SERVICE) {
            if (sServer!=null) {
                sServer.destroy();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

}

