package com.cuong.filemanage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Cuong on 3/29/2018.
 */

public class ListAdapter extends ArrayAdapter<FileModel> {


    int selectedItemPostion;
    Context mContext;
    public ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FileModel> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        FileModel file = getItem(position);
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.list_item, null);

        }
        ImageView img = v.findViewById(R.id.item_icon);
        TextView tvName = v.findViewById(R.id.item_name);
        TextView tvTime = v.findViewById(R.id.tv_item_time);
        TextView tvSize = v.findViewById(R.id.tv_item_size);
        LinearLayout line = v.findViewById(R.id.item_holder);
        final ImageView menuButton = v.findViewById(R.id.item_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(menuButton, position);
            }
        });
        line.setSelected(true);

        if (tvName != null) {
            tvName.setText(file.getName());
        }

        if (tvTime != null) {
            tvTime.setText(file.getTime());
        }
        if (tvSize != null) {
            tvSize.setText(file.extra);
        }
        switch (file.getType()){
            case Constants.APP_FILE_TYPE:
                img.setImageResource(R.drawable.ic_apk);
                break;
            case Constants.COMPRESSED_FILE_TYPE:
                img.setImageResource(R.drawable.ic_zip);
                break;
            case Constants.PHOTO_FILE_TYPE:
                img.setImageResource(R.drawable.ic_photo);
                break;
            case Constants.VIDEO_FILE_TYPE:
                img.setImageResource(R.drawable.ic_video);
                break;
            case Constants.MUSIC_FILE_TYPE:
                img.setImageResource(R.drawable.ic_music);
                break;
            case Constants.DOCUMENT_FILE_TYPE:
                img.setImageResource(R.drawable.ic_other_file);
                break;
            case Constants.TEXT_FILE_TYPE:
                img.setImageResource(R.drawable.ic_other_file);
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

    private void showPopupMenu(View view, int postion) {
        // inflate menu
        selectedItemPostion = postion;
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_item_delete:
                    Toast.makeText(mContext, "Delete", Toast.LENGTH_SHORT).show();
                    FileManager.deleteDirectory(new File(getItem(selectedItemPostion).getPath()));
                    remove(getItem(selectedItemPostion));
                    notifyDataSetChanged();
                    return true;
                case R.id.action_item_info:
                    new InfoDialog(mContext, getItem(selectedItemPostion).getPath()).show();
                    return true;
                case R.id.action_item_rename:
                    new RenameDialog(mContext, new File(getItem(selectedItemPostion).getPath())).show();
                    return true;
                case R.id.action_item_copy:
                    ArrayList<FileModel> fileToCopy = new ArrayList<FileModel>();
                    fileToCopy.add(getItem(selectedItemPostion));
                    ((MainActivity) (mContext)).setmClipBoard(fileToCopy, Constants.CLIPBOARD_ACTION_COPY);
                    ((MainActivity) (mContext)).invalidateOptionsMenu();
                    return true;
                case R.id.action_item_move:
                    ArrayList<FileModel> fileToMove = new ArrayList<FileModel>();
                    fileToMove.add(getItem(selectedItemPostion));
                    ((MainActivity) (mContext)).setmClipBoard(fileToMove, Constants.CLIPBOARD_ACTION_MOVE);
                    ((MainActivity) (mContext)).invalidateOptionsMenu();
                    return true;
                default:
            }

            return false;

        }

    }
  }