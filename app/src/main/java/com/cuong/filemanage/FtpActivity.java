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
import com.cuong.filemanage.Ultility.Constant;

import java.io.IOException;

public class FtpActivity extends AppCompatActivity {
    FTPServer msever;
    Button btnStart;
    TextView tvStatus;
    EditText edtUsername;
    EditText edtPassword;
    EditText edtPath;
    EditText edtPort;
    FTPServer ftpServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);
        getSupportActionBar().setTitle("FTP Server");
        initViews();
    }
    public void initViews(){
        tvStatus = findViewById(R.id.tv_fpt_status);
        edtUsername = findViewById(R.id.edt_ftp_username);
        edtPassword = findViewById(R.id.edt_ftp_password);
        edtPath = findViewById(R.id.edt_ftp_path);
        edtPort = findViewById(R.id.edt_ftp_port);
        btnStart = findViewById(R.id.btn_ftp_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ftpServer==null){
                    try {
                        ftpServer= new FTPServer(Integer.parseInt(edtPort.getText().toString()));
                        ftpServer.start();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Can't start FTP server on this port",Toast.LENGTH_LONG).show();
                        return;
                    }
                    tvStatus.setText("Running");
                    btnStart.setText("Stop");
                    return;
                }
                if (ftpServer.isRunning()){
                    ftpServer.destroy();
                    btnStart.setText("Start");
                    tvStatus.setText("Not running");
                    return;

                }
                if (!ftpServer.isRunning()){
                    try {
                        ftpServer = new FTPServer(Integer.parseInt(edtPort.getText().toString()));
                        ftpServer.start();
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
        if (ftpServer!=null&&ftpServer.isRunning()){
            ftpServer.destroy();
            Toast.makeText(this,"FTP server stop!",Toast.LENGTH_LONG).show();
            ftpServer = null;
        }
        super.onBackPressed();
    }
}
