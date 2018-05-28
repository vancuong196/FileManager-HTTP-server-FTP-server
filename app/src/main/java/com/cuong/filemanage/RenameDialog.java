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

public class RenameDialog extends Dialog implements View.OnClickListener {

    String path;
    File currentFile;
    String currentname;
    EditText edtName;
    private Context context;

    public RenameDialog(@NonNull Context context, File path) {
        super(context);
        this.context = context;
        this.currentFile = path;
        this.path = path.getParent();
        currentname = path.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_folder);
        Button btnOk = findViewById(R.id.btn_dialog_ok);
        Button btnCancel = findViewById(R.id.btn_dialog_cancel);
        edtName = findViewById(R.id.edt_folder_name);
        edtName.setText(currentname);
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
                if (name.equals(currentname)) {
                    dismiss();
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
                File newFile = new File(path + "/" + name);
                if (newFile.exists() && currentFile.isDirectory() && newFile.isDirectory()) {
                    Toast.makeText(context, "Folder name exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newFile.exists() && currentFile.isFile() && newFile.isFile()) {
                    Toast.makeText(context, "File name exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentFile.renameTo(newFile);
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

