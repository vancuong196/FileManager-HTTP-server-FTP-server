package com.cuong.filemanage;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    String mCurrentPath;
    String mCurrentRoot;
    ListView mLvFile;
    ArrayList<FileModel> mToDeleteFileList;
    ArrayList<FileModel> mClipBoardFileList;
    int clipBoardAction = Constants.CLIPBOARD_ACTION_EMPTY;
    ArrayList<FileModel> mFileList;
    ListAdapter mAdapter;
    FloatingActionButton fab;
    TextView tvPath;
    ImageView backButton;
    boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.btn_add);
        tvPath = findViewById(R.id.tv_path);
        backButton = findViewById(R.id.img_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mCurrentPath + "  " + mCurrentRoot);
                if (mCurrentPath.equals(mCurrentRoot)) {
                    return;
                }
                if (mCurrentPath != null) {
                    mCurrentPath = new File(mCurrentPath).getParent();
                    refreshListViewFilesWithPath(mCurrentPath);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewFileDialog(MainActivity.this, mCurrentPath).show();
            }
        });
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sw_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListViewFilesWithPath(mCurrentPath);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        listViewFilesInit();
        spinnerInit();
    }

    public void setmClipBoard(ArrayList<FileModel> fileModels, int clipBoardAction) {
        this.mClipBoardFileList = fileModels;
        this.clipBoardAction = clipBoardAction;
    }

    public int getClipBoardAction() {
        return clipBoardAction;
    }

    public ArrayList<FileModel> getmClipBoardFileList() {
        if (mClipBoardFileList == null) {
            return new ArrayList<>();
        } else {
            return mClipBoardFileList;
        }

    }

    public void clearClipboard() {
        mClipBoardFileList = null;
        this.clipBoardAction = Constants.CLIPBOARD_ACTION_EMPTY;
    }

    public void listViewFilesInit() {
            mLvFile = findViewById(R.id.lv_file);
            mFileList = FileManager.getFileList( Environment.getExternalStorageDirectory().toString());
            mAdapter =new ListAdapter(this,R.layout.list_item,mFileList);
            mLvFile.setAdapter(mAdapter);
            mLvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mAdapter.getItem(position).getType()==Constants.FOLDER_TYPE){
                        System.out.println("debug");
                        mCurrentPath = mAdapter.getItem(position).getPath();
                        refreshListViewFilesWithPath(mAdapter.getItem(position).getPath());
                    }
                    else {
                        String type = "";
                        switch (mAdapter.getItem(position).getType()) {
                            case Constants.APP_FILE_TYPE:
                                type = "application/vnd.android.package-archive";
                                break;
                            case Constants.COMPRESSED_FILE_TYPE:
                                type = "application/zip";
                                break;
                            case Constants.PHOTO_FILE_TYPE:
                                type = "image";
                                break;
                            case Constants.VIDEO_FILE_TYPE:
                                type = "video";
                                break;
                            case Constants.MUSIC_FILE_TYPE:
                                type = "music";
                                break;
                            case Constants.DOCUMENT_FILE_TYPE:
                                type = "unknown";
                                break;
                            case Constants.TEXT_FILE_TYPE:
                                type = "unknown";
                                break;
                            case Constants.OTHER_FILE_TYPE:
                                type = "unknown";
                                break;
                        }
                        if ("unknown".equals(type)) {
                            Toast.makeText(getApplication(), "Not supported!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(mAdapter.getItem(position).getPath())), type);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getApplication(), "Not supported!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }}
            });
            mLvFile.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mLvFile.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    mode.setTitle(mLvFile.getCheckedItemCount()
                            + " items");

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.cab_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                private void doCopyAction() {
                    ArrayList<FileModel> files = getCurrentSelectedFiles();
                    setmClipBoard(files, Constants.CLIPBOARD_ACTION_COPY);
                    invalidateOptionsMenu();
                }

                private void doMoveAction() {
                    ArrayList<FileModel> files = getCurrentSelectedFiles();
                    setmClipBoard(files, Constants.CLIPBOARD_ACTION_MOVE);
                    invalidateOptionsMenu();
                }


                private void doDeleteOption() {
                    mToDeleteFileList = getCurrentSelectedFiles();
                    showConfirmDeleteDialog();
                }
                //            called to report a user click on an action item
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.cab_copy:
                            doCopyAction();
                            mode.finish();
                            return true;
                        case R.id.cab_delete:
                            doDeleteOption();
                            mode.finish();
                            return true;
                        case R.id.cab_move:
                            doMoveAction();
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                //called when an action mode is about to be exited and destroyed
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
    }

    public void spinnerInit() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> s = getStorageDirectories();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,s);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPath= mCurrentRoot = (String)arrayAdapter.getItem(position);
                refreshListViewFilesWithPath((String) arrayAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public synchronized ArrayList<String> getStorageDirectories() {
        File[] list = list = getApplicationContext().getExternalFilesDirs(null);
        ArrayList<String> storages =new ArrayList<>();
        for (File aList : list) {
            storages.add(aList.getParentFile().getParentFile().getParentFile().getParentFile().getPath());
        }
        return storages;
    }

    public ArrayList<FileModel> getCurrentSelectedFiles() {
        SparseBooleanArray checked = mLvFile.getCheckedItemPositions();
        ArrayList<FileModel> files = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i) == true) {
                FileModel file = (FileModel) mLvFile.getItemAtPosition(checked.keyAt(i));
                files.add(file);
            }
        }
        return files;
    }

    public void refreshListViewFilesWithPath(String path) {
        tvPath.setText(path);
        mCurrentPath = path;
        mFileList = FileManager.getFileList(path);
        mAdapter.clear();
        mAdapter.addAll(mFileList);
        mAdapter.notifyDataSetChanged();
    }

    public void refreshListView() {
        mFileList = FileManager.getFileList(mCurrentPath);
        mAdapter.clear();
        mAdapter.addAll(mFileList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        System.out.println(mCurrentPath+"  "+mCurrentRoot);
        if (mCurrentPath.equals(mCurrentRoot)){
            if (isExit) {
                finish();
            } else {
                isExit = true;
                Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            }
            return;
        }
        if (mCurrentPath!=null){
            mCurrentPath = new File(mCurrentPath).getParent();
            refreshListViewFilesWithPath(mCurrentPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        if (getmClipBoardFileList().size() == 0) {
            menu.findItem(R.id.action_paste).setVisible(false);
            menu.findItem(R.id.action_clipboard_clear).setVisible(false);
        } else {
            menu.findItem(R.id.action_paste).setVisible(true);
            menu.findItem(R.id.action_clipboard_clear).setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_http_server){
            Intent myIntent = new Intent(this,HttpServerActivity.class);
            startActivity(myIntent);
            return true;
        } else if (id ==R.id.action_exit){
            finish();
        } else if (id == R.id.action_ftp_server) {
            Intent myIntent = new Intent(this,FtpActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.action_paste) {
            if (getClipBoardAction() == Constants.CLIPBOARD_ACTION_COPY) {

                copyFilesFromClipboardToPath(getmClipBoardFileList(), mCurrentPath);
                clearClipboard();
                invalidateOptionsMenu();
            } else if (getClipBoardAction() == Constants.CLIPBOARD_ACTION_MOVE) {
                moveFilesFromClipboardToPath(getmClipBoardFileList(), mCurrentPath);
                clearClipboard();
                invalidateOptionsMenu();
            }
        } else if (id == R.id.action_clipboard_clear) {
            clearClipboard();
            invalidateOptionsMenu();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfirmDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)

                //set title
                .setTitle("Are you sure to delete?")
                //set message
                .setMessage("You won't able to use it anymore")
                //set positive button
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFiles(mToDeleteFileList);
                        return;
                    }
                })
                //set negative button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mToDeleteFileList = null;
                        return;
                    }
                })
                .show();
    }

    public void deleteFiles(ArrayList<FileModel> files) {
        new DeleteFileTask().execute(files);
    }

    public void copyFilesFromClipboardToPath(ArrayList<FileModel> files, String destinationPath) {
        ArrayList<String> filePaths = new ArrayList<>();
        ArrayList<String> destinationPaths = new ArrayList<>();
        destinationPaths.add(destinationPath);
        for (FileModel item : files
                ) {
            filePaths.add(item.getPath());
        }
        new CopyFileTask().execute(filePaths, destinationPaths);
    }

    public void moveFilesFromClipboardToPath(ArrayList<FileModel> files, String destinationPath) {
        ArrayList<String> filePaths = new ArrayList<>();
        ArrayList<String> destinationPaths = new ArrayList<>();
        destinationPaths.add(destinationPath);
        for (FileModel item : files
                ) {
            filePaths.add(item.getPath());
        }
        new MoveFileTask().execute(filePaths, destinationPaths);
    }

    private class DeleteFileTask extends AsyncTask<ArrayList<FileModel>, Void, Boolean> {


        @Override
        protected Boolean doInBackground(ArrayList<FileModel>[] arrayLists) {
            ArrayList<FileModel> files = arrayLists[0];
            for (FileModel item : files
                    ) {
                System.out.println(item.getPath());
                FileManager.deleteDirectory(new File(item.getPath()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Toast.makeText(getApplicationContext(), "Delected!", Toast.LENGTH_SHORT).show();
            refreshListViewFilesWithPath(mCurrentPath);
        }
    }

    private class CopyFileTask extends AsyncTask<ArrayList<String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<String>[] arrayLists) {
            ArrayList<String> filePaths = arrayLists[0];
            String destination = arrayLists[1].get(0);
            for (int i = 0; i < filePaths.size(); i++) {
                try {
                    FileManager.copyDirectory(new File(filePaths.get(i)), new File(destination));
                } catch (Exception e) {
                    System.out.println(e.toString());
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                refreshListViewFilesWithPath(mCurrentPath);
                Toast.makeText(getApplicationContext(), "Copy files completed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Copy files error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MoveFileTask extends AsyncTask<ArrayList<String>, Void, Boolean> {
        ArrayList<String> filePaths;

        @Override
        protected Boolean doInBackground(ArrayList<String>[] arrayLists) {
            filePaths = arrayLists[0];
            String destination = arrayLists[1].get(0);
            for (int i = 0; i < filePaths.size(); i++) {
                try {
                    FileManager.copyDirectory(new File(filePaths.get(i)), new File(destination));
                } catch (Exception e) {
                    System.out.println(e.toString());
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                for (int i = 0; i < filePaths.size(); i++) {
                    try {
                        FileManager.deleteDirectory(new File(filePaths.get(i)));
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
                if (getApplicationContext() != null) {
                    Toast.makeText(getApplicationContext(), "Move file complete files completed!", Toast.LENGTH_SHORT).show();
                    refreshListViewFilesWithPath(mCurrentPath);
                }
            } else {
                if (getApplicationContext() != null)
                    Toast.makeText(getApplicationContext(), "Move files error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
