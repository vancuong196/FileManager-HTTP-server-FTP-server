package com.cuong.filemanage.Service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;

import com.cuong.filemanage.HttpServer.HttpServer;
import com.cuong.filemanage.Ultility.Constant;

public class WebService extends Service {
    private static HttpServer sServer;
    private static int sPort;
    private static String sPath;
    public WebService() {
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
            sPort = intent.getIntExtra(Constant.EXTRA_PORT,8080);
            sPath = intent.getStringExtra(Constant.EXTRA_PATH);
            AssetManager assetManager = getAssets();
            if (sServer==null){
                sServer = new HttpServer(9999,assetManager);
                sServer.start();
            }
        }
        if (action == Constant.ACTION_STOP_SERVICE) {
            if (sServer!=null) {
                sServer.stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
