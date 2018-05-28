package com.cuong.filemanage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;


/**
 * Created by Cuong on 3/25/2018.
 */

public class InfoDialog extends Dialog implements View.OnClickListener {

    TextView tvName, tvSize, tvPath;
    String path;
    private Context context;

    public InfoDialog(@NonNull Context context, @NonNull String path) {
        super(context);
        this.context = context;
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.info_layout);
        File file = new File(path);

        tvName = findViewById(R.id.tv_name);
        tvPath = findViewById(R.id.tv_path);
        tvSize = findViewById(R.id.tv_size);
        boolean isfolder = file.isDirectory();
        tvName.setText(file.getName());
        tvPath.setText(file.getPath());
        if (isfolder) {
            tvSize.setText(String.valueOf(file.listFiles().length) + " items");
        } else {
            tvSize.setText(Utils.size(file.length()));
        }


        Button btnCancel = findViewById(R.id.btn_dialog_cancel);

        btnCancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}

