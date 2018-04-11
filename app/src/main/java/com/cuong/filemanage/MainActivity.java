package com.cuong.filemanage;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
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
import android.widget.AbsListView;
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
    String mPrevious;
    ListView mLvFile;
    ArrayList<FileModel> mFileList;
    ListAdapter mAdapter;
    Adapter2 adapter2;
    private boolean isAddFour = false;
    private boolean isRemoveFour = false;
    ListView listView2;
    HttpServer mServer;
    Spinner spinner;
    ArrayList<DirectoryModel> directoryModels;
    String TAG = "DEbug_Info";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AssetManager assetManager = getAssets();
        mServer = new HttpServer(9999, assetManager);
        mServer.start();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sw_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(mCurrentPath);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        init();
        init2();
        init3();
    }
    public void init(){
            mLvFile = findViewById(R.id.lv_file);
            mPrevious=mCurrentPath = Environment.getExternalStorageDirectory().toString();

            mFileList = FileManager.getFileList( Environment.getExternalStorageDirectory().toString());
            mAdapter =new ListAdapter(this,R.layout.list_item,mFileList);
            mLvFile.setAdapter(mAdapter);
            mLvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mAdapter.getItem(position).getType()==Constants.FOLDER_TYPE){
                        System.out.println("debug");

                        refresh(mAdapter.getItem(position).getPath());
                    }
                    else {
                        Snackbar.make(view, "Not support yet!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }}
            });
            mLvFile.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mLvFile.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                //            called when an item is checked/unchecked during selection mode
                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    mode.setTitle(mLvFile.getCheckedItemCount()
                            + " items");
                }

                /* called when the action mode is first created.
                 The supplied menu is used for action buttons*/
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    Log.i(TAG, "creating action mode");
                    MenuInflater inflater = mode.getMenuInflater();
                    getSupportActionBar().hide();
                    inflater.inflate(R.menu.cad_menu, menu);
                    menu.findItem(R.id.cab_four).setVisible(false);
                    return true;
                }

                //called to refresh action mode action menu whenever it is invalidated
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
                    getSupportActionBar().show();
                    isRemoveFour = false;
                    isAddFour = false;
                }
            });
            System.out.println(getStorageDirectories());
    }
    public void init2(){
        listView2 = findViewById(R.id.lv_dir_navigation);
        directoryModels = new ArrayList<>();
        ArrayList<String> s = getStorageDirectories();
        for (int i=0; i< s.size();i++){
            File file = new File(s.get(i));
            String size = Utils.size(file.getTotalSpace());
            String path = file.getPath();
            String name = file.getName();

            directoryModels.add(new DirectoryModel(name,path,size));
        }
        adapter2 = new Adapter2(this,R.layout.list_item2,directoryModels);
        listView2.setAdapter(adapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path=adapter2.getItem(position).getPath();
                refresh(path);
            }
        });
    }
    public void init3(){
        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<String> s = getStorageDirectories();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,s);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
    }
    public static final Pattern DIR_SEPARATOR = Pattern.compile("/");
    public synchronized ArrayList<String> getStorageDirectories() {
        // Final set of paths
        final ArrayList<String> rv = new ArrayList<>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }


        return rv;
    }

    public void refresh(String path) {
        mPrevious = mCurrentPath;
        mCurrentPath = path;
        mFileList = FileManager.getFileList(path);
        mAdapter.clear();
        mAdapter.addAll(mFileList);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (mPrevious!=null){
            refresh(new File(mCurrentPath).getParent());
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
