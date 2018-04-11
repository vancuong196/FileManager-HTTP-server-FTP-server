package com.cuong.filemanage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cuong.filemanage.FtpServer.FTPServer;
import com.cuong.filemanage.Service.FtpServerService;
import com.cuong.filemanage.Service.WebService;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);
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
                Intent mIntent = new Intent(getApplication(), FtpServerService.class);
                mIntent.setAction(Constant.ACTION_START_SERVICE);
                startService(mIntent);
            }
        });
    }
}
