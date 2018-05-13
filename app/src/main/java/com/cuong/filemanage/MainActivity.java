package com.cuong.filemanage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.cuong.filemanage.HttpServer.HttpServer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {
    String mCurrentPath;
    String mCurrentRoot;
    ListView mLvFile;
    ArrayList<FileModel> mFileList;
    ListAdapter mAdapter;
    private boolean isAddFour = false;
    private boolean isRemoveFour = false;
    String TAG = "DEbug_Info";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sw_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(mCurrentPath);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        init();
        init3();
    }
    public void init(){
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
                        refresh(mAdapter.getItem(position).getPath());
                    }
                    else {
                        Snackbar.make(view, "Not support yet!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }}
            });
            mLvFile.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mLvFile.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    mode.setTitle(mLvFile.getCheckedItemCount()
                            + " items");
                    SparseBooleanArray a =mLvFile.getCheckedItemPositions();
                    if (a.get(position)){
                        mAdapter.getItem(position).setSelected(true);
                    } else {
                        mAdapter.getItem(position).setSelected(false);
                    }

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    Log.i(TAG, "creating action mode");
                    MenuInflater inflater = mode.getMenuInflater();
                   // getSupportActionBar().hide();
                    inflater.inflate(R.menu.cad_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    Log.i(TAG, "preparing action mode");
                    if (isAddFour) {
//                    set menu item Four visible
                        Log.i(TAG, "adding menu item Four");
                        menu.findItem(R.id.cab_four).setVisible(true);
                        return true;
                    } else if (isRemoveFour) {
//                    set menu item four invisible
                        Log.i(TAG, "removing menu item Four");
                        menu.findItem(R.id.cab_four).setVisible(false);

                        return true;
                    } else {
//            return false if nothing is done
                        return false;
                    }
                }

                //            called to report a user click on an action item
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.cab_copy:
                            Log.i(TAG, "action item ONE clicked");
                            doSomethingWithActionOneItems();
                            // Action picked, so close the CAB
                            mode.finish();
                            return true;
                        case R.id.cab_delete:
                            Log.i(TAG, "action item TWO clicked");
                            doSomethingWithActionTwoItems();
                            // Action picked, so close the CAB
                            mode.finish();
                            return true;
                        case R.id.cab_three:
                            Log.i(TAG, "clicked item three");
                            isAddFour = true;
                            isRemoveFour = false;
                            //invalidate the action mode and refresh the menu content
                            mode.invalidate();
                            return true;
                        case R.id.sub_one:
                            Log.i(TAG, "Clicked sub one");
                            // Action picked, so close the CAB
                            mode.finish();
                            return true;
                        case R.id.sub_two:
                            Log.i(TAG, "Clicked sub two");
                            // Action picked, so close the CAB
                            mode.finish();
                            return true;
                        case R.id.cab_four:
                            Log.i(TAG, "clicked item four");
                            isRemoveFour = true;
                            isAddFour = false;
//                      invalidate the action mode and refresh the menu content
                            mode.invalidate();
                            return true;
                        default:
                            return false;
                    }
                }

                //called when an action mode is about to be exited and destroyed
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    Log.i(TAG, "destroying action mode");
                 //   getSupportActionBar().show();
                    isRemoveFour = false;
                    isAddFour = false;
                }
            });
            System.out.println(getStorageDirectories());
    }

    public void init3(){
        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> s = getStorageDirectories();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,s);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPath= mCurrentRoot = (String)arrayAdapter.getItem(position);
                refresh((String)arrayAdapter.getItem(position));
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
            System.out.println("Path----------------" + aList.getParentFile().getParentFile().getParentFile().getParentFile().getPath());
            storages.add(aList.getParentFile().getParentFile().getParentFile().getParentFile().getPath());
        }
        return storages;
    }


    public void refresh(String path) {
        mCurrentPath = path;
        mFileList = FileManager.getFileList(path);
        mAdapter.clear();
        mAdapter.addAll(mFileList);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        System.out.println(mCurrentPath+"  "+mCurrentRoot);
        if (mCurrentPath.equals(mCurrentRoot)){
            return;
        }
        if (mCurrentPath!=null){
            mCurrentPath = new File(mCurrentPath).getParent();
            refresh(mCurrentPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
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
        }
        else if (id == R.id.action_ftp_server) {
            Intent myIntent = new Intent(this,FtpActivity.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void doSomethingWithActionTwoItems() {
        Log.i(TAG, "getting action two items");
        SparseBooleanArray checked = mLvFile.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i) == true) {
                String theSelectedCountry = (String) mLvFile
                        .getItemAtPosition(checked.keyAt(i));
                Log.i(TAG, "Selected country key: " + checked.keyAt(i) + " country: " + theSelectedCountry);
            }
        }
    }

    private void doSomethingWithActionOneItems() {
        Log.i(TAG, "getting action one items");
        mLvFile.getCheckedItemIds();
        SparseBooleanArray checked = mLvFile.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i) == true) {
                String theSelectedCountry = (String) mLvFile
                        .getItemAtPosition(checked.keyAt(i));
                Log.i(TAG, "Selected country key: " + checked.keyAt(i) + " country: " + theSelectedCountry);
            }
        }
    }
}
