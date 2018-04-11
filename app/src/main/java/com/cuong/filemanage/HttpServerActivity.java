package com.cuong.filemanage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cuong.filemanage.Service.WebService;
import com.cuong.filemanage.Ultility.Constant;

public class HttpServerActivity extends AppCompatActivity {
    EditText edtPath;
    EditText edtPort;
    Button btnStart;
    TextView tvStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.http_activity);
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
                Intent mIntent = new Intent(getApplication(), WebService.class);
                mIntent.setAction(Constant.ACTION_START_SERVICE);
                startService(mIntent);
            }
        });
    }
}
