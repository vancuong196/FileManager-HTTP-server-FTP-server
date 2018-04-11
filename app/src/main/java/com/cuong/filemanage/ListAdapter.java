package com.cuong.filemanage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Cuong on 3/29/2018.
 */

public class ListAdapter extends ArrayAdapter<FileModel> {

    public ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FileModel> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        FileModel file = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.list_item, null);

        }
        ImageView img = v.findViewById(R.id.item_icon);
        TextView tt1 = (TextView) v.findViewById(R.id.item_name);
        TextView tt2 = (TextView) v.findViewById(R.id.tv_item_time);
        TextView tt3 = (TextView) v.findViewById(R.id.tv_item_size);


        if (tt1 != null) {
            tt1.setText(file.getName());
        }

        if (tt2 != null) {
            tt2.setText(file.getTime());
        }
        if (tt3 != null) {
            tt3.setText(file.extra);
        }
       switch (file.getType()){
           case Constants.APP_FILE_TYPE:
               img.setImageResource(R.drawable.ic_other_file);
               break;
           case Constants.COMPRESSED_FILE_TYPE:
               img.setImageResource(R.drawable.folder_icon);
               break;
           case Constants.PHOTO_FILE_TYPE:
               img.setImageResource(R.drawable.ic_image);
               break;
           case Constants.VIDEO_FILE_TYPE:
               img.setImageResource(R.drawable.folder_icon);
               break;
           case Constants.MUSIC_FILE_TYPE:
               img.setImageResource(R.drawable.ic_music);
               break;
           case Constants.DOCUMENT_FILE_TYPE:
               img.setImageResource(R.drawable.folder_icon);
               break;
           case Constants.TEXT_FILE_TYPE:
               img.setImageResource(R.drawable.folder_icon);
               break;
           case Constants.FOLDER_TYPE:
               img.setImageResource(R.drawable.folder_icon);
               break;
           case Constants.OTHER_FILE_TYPE:
               img.setImageResource(R.drawable.ic_other_file);
               break;



       }

        // Return the completed view to render on screen
        return v;
    }
}
