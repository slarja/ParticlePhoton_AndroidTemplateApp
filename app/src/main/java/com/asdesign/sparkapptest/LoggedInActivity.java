package com.asdesign.sparkapptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class LoggedInActivity extends AppCompatActivity {

    ParticleCloud localSparkCloud = ParticleCloudSDK.getCloud();
    List<ParticleDevice> localSparkDevices;
    ListView listView;
    private final static String LOGGED_IN_TAG = "SPARK_TEST_LOG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGGED_IN_TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        // Retrieve the list of connected devices
        retrieveAllDevices(findViewById(R.id.loggedInMainView));

        // Get the list view object
        listView = (ListView) findViewById(R.id.loggedInListView);

        // Display the devices which are connected
        if(localSparkDevices != null) {

            // Copy the names of all devices to an array
            List<String> localSparkDevicesStrings = new ArrayList<String>();

            Iterator<ParticleDevice> iteratorOverDevices = localSparkDevices.iterator();
            while (iteratorOverDevices.hasNext()) {
                localSparkDevicesStrings.add(iteratorOverDevices.next().getName().toString());
            }

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            ArrayAdapter<String> localSparkDevicesAdapter = new ArrayAdapter<String>(LoggedInActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, localSparkDevicesStrings);

            listView.setAdapter(localSparkDevicesAdapter);
        }
        else
        {
            Toaster.l(LoggedInActivity.this, "No devices");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief This function retrieves the list of connected devices from the cloud
     * @param myView specifies the view from which this is being called
     */
    private void retrieveAllDevices(View myView){

        // List out all connected devices
        Async.executeAsync(localSparkCloud, new Async.ApiWork<ParticleCloud, List<ParticleDevice>>() {

            public List<ParticleDevice> callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                return sparkCloud.getDevices();
            }

            @Override
            public void onSuccess(List<ParticleDevice> devices) {
                localSparkDevices = devices;
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                //Log.e("SOME_TAG", e);
                localSparkDevices = null;
                Log.i(LOGGED_IN_TAG, "Call onFailure()");
                Toaster.l(LoggedInActivity.this, "Wrong credentials or no internet connectivity, please try again");
            }
        });
    }
}
