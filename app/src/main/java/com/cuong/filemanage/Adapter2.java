package com.cuong.filemanage;

/**
 * Created by Cuong on 3/31/2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuong.filemanage.Constants;
import com.cuong.filemanage.FileModel;
import com.cuong.filemanage.R;

import java.util.ArrayList;

/**
 * Created by Cuong on 3/29/2018.
 */

public class Adapter2 extends ArrayAdapter<DirectoryModel> {

    public Adapter2(@NonNull Context context, int resource, @NonNull ArrayList<DirectoryModel> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        DirectoryModel file = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.list_item2, null);

        }
        ImageView img = v.findViewById(R.id.item_icon);
        TextView tt1 = (TextView) v.findViewById(R.id.item_name2);
        TextView tt2 = (TextView) v.findViewById(R.id.tv_item_path2);
        TextView tt3 = (TextView) v.findViewById(R.id.tv_item_size2);


        if (tt1 != null) {
            tt1.setText(file.getName());
        }

        if (tt2 != null) {
            tt2.setText(file.getPath());
        }
        if (tt3 != null) {
            tt3.setText(file.getSize());
        }




        // Return the completed view to render on screen
        return v;
    }
}
