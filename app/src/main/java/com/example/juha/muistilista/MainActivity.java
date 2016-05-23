package com.example.juha.muistilista;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final String PREFS_TASKS = "prefs_tasks";
    private final String KEY_TASKS_LIST = "list";

    private BroadcastReceiver mTickReceiver;
    private static final String TAG = MainActivity.class.getName();
    private final int ADD_TASK_REQUEST = 1;

    private ArrayList<String> mList;
    private ArrayAdapter<String> mAdapter;
    private TextView mDateTimeTextView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1
        super.onCreate(savedInstanceState);

        // 2 -Make the activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 3
        setContentView(R.layout.activity_main);

        // 4
        mDateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
        final Button addTaskBtn = (Button) findViewById(R.id.addTaskBtn);
        final ListView listview = (ListView) findViewById(R.id.taskListview);
        mList = new ArrayList<String>();

        String savedList = getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).getString(KEY_TASKS_LIST, null);
        if (savedList != null) {
            String[] items = savedList.split(",");
            mList = new ArrayList<String>(Arrays.asList(items));
        }
        // 5
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        listview.setAdapter(mAdapter);

        // 6
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                taskSelected(i);
            }
        });

        mTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    mDateTimeTextView.setText(getCurrentTimeStamp());
                }
            }
        };
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        // 1
        super.onResume();
        // 2
        mDateTimeTextView.setText(getCurrentTimeStamp());
        // 3
        registerReceiver(mTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        // 4
        super.onPause();
        // 5
        if (mTickReceiver != null) {
            try {
                unregisterReceiver(mTickReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Timetick Receiver not registered", e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.juha.muistilista/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        // Save all data which you want to persist.
        StringBuilder savedList = new StringBuilder();
        for (String s : mList) {
            savedList.append(s);
            savedList.append(",");
        }
        getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).edit()
                .putString(KEY_TASKS_LIST, savedList.toString()).commit();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public void addTaskClicked(View view) {
        Intent intent = new Intent(MainActivity.this, TaskDescriptionActivity.class);
        startActivityForResult(intent, ADD_TASK_REQUEST);
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 1 - Check which request you're responding to
        if (requestCode == ADD_TASK_REQUEST) {
            // 2 - Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // 3 - The user entered a task. Add a task to the list.
                String task = data.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION);
                mList.add(task);
                // 4
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void taskSelected(final int position) {
        // 1
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // 2
        alertDialogBuilder.setTitle(R.string.alert_title);

        // 3
        alertDialogBuilder
                .setMessage(mList.get(position))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // 4
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 5
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.juha.muistilista/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
}

