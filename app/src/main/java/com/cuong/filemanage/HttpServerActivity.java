package com.cuong.filemanage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cuong.filemanage.FtpServer.FTPServer;
import com.cuong.filemanage.HttpServer.HttpServer;


import java.io.IOException;

public class HttpServerActivity extends AppCompatActivity {
    EditText edtPath;
    EditText edtPort;
    Button btnStart;
    TextView tvStatus;
    HttpServer httpServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_activity);
        getSupportActionBar().setTitle("HTTP Server");
       initViews();

    }
    public void initViews(){
        tvStatus = findViewById(R.id.tv_http_status);
        edtPath = findViewById(R.id.edt_http_path);
        edtPort = findViewById(R.id.edt_http_port);
        btnStart = findViewById(R.id.btn_http_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (httpServer==null){
                    try {
                        httpServer= new HttpServer(Integer.parseInt(edtPort.getText().toString()),getAssets());
                        httpServer.start();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Can't start HTTP server on this port",Toast.LENGTH_LONG).show();
                        return;
                    }
                    tvStatus.setText("Running");
                    btnStart.setText("Stop");
                    return;
                }
                if (httpServer.isRunning()){
                    httpServer.stop();
                    btnStart.setText("Start");
                    tvStatus.setText("Not running");
                    return;

                }
                if (!httpServer.isRunning()){
                    try {
                        httpServer = new HttpServer(Integer.parseInt(edtPort.getText().toString()),getAssets());
                        httpServer.start();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Cant not start FTP server",Toast.LENGTH_LONG);
                        return;
                    }
                    tvStatus.setText("Running");
                    btnStart.setText("Stop");
                    return;
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (httpServer!=null&&httpServer.isRunning()){
            httpServer.stop();
            Toast.makeText(this,"FTP server stop!",Toast.LENGTH_LONG).show();
            httpServer = null;
        }
        super.onBackPressed();
    }
}
