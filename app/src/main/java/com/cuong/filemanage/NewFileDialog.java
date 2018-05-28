package com.cuong.filemanage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;


/**
 * Created by Cuong on 3/25/2018.
 */

public class NewFileDialog extends Dialog implements View.OnClickListener {
    String path;
    EditText edtName;
    private Context context;

    public NewFileDialog(@NonNull Context context, String path) {
        super(context);
        this.context = context;
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_folder);
        Button btnOk = findViewById(R.id.btn_dialog_ok);
        Button btnCancel = findViewById(R.id.btn_dialog_cancel);
        edtName = findViewById(R.id.edt_folder_name);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_dialog_ok:
                String name = edtName.getText().toString();

                if (edtName.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Enter name first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String specialCharacters = "!#$%&'()*,/:;<=>?@[]^`{|}~";
                String[] strlCharactersArray = new String[name.length()];
                for (int i = 0; i < name.length(); i++) {
                    strlCharactersArray[i] = Character
                            .toString(name.charAt(i));
                }
                //now  strlCharactersArray[i]=[A, d, i, t, y, a]
                int count = 0;
                for (int i = 0; i < strlCharactersArray.length; i++) {
                    if (specialCharacters.contains(strlCharactersArray[i])) {
                        count++;
                    }

                }

                if (count != 0) {
                    Toast.makeText(context, "File name contain special character!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newFilePath = path + "/" + name;

                File file = new File(newFilePath);
                System.out.println(newFilePath);
                if (file.exists()) {
                    Toast.makeText(context, "Folder is exist!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!file.mkdir()) {
                    Toast.makeText(context, "Error when create new folder!", Toast.LENGTH_SHORT).show();
                }
                ((MainActivity) (context)).refreshListView();
                break;

            case R.id.btn_dialog_cancel:
                dismiss();

                break;
            default:
                break;
        }
        dismiss();
    }

}

