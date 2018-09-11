package com.esi.easyorder.activites;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.dd.processbutton.iml.ActionProcessButton;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.services.ServerService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MerchantActivity extends AppCompatActivity {
    ServerService serverService;
    boolean mBound = false;

    EditText mName;
    EditText mPhoneNumber;
    EditText mCity;
    EditText mStoreType;
    EditText mStoreName;
    FancyButton registerButton;
    ActionProcessButton locateMe;


    boolean located;
    Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        mName = findViewById(R.id.mName);
        mPhoneNumber = findViewById(R.id.mPhoneNumber);
        mCity = findViewById(R.id.mCity);
        mStoreType = findViewById(R.id.mStoreType);
        mStoreName = findViewById(R.id.mStoreName);
        registerButton = findViewById(R.id.mRegisterButton);
        locateMe = findViewById(R.id.mGetLocation);
    }

    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, ServerService.class);
        bindService(service, mConnection, BIND_AUTO_CREATE);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ownerName, ownerPhone, ownerCity, ownerStoreType, ownerStoreName;
                ownerName = mName.getText().toString();
                ownerPhone = mPhoneNumber.getText().toString();
                ownerCity = mCity.getText().toString();
                ownerStoreType = mStoreType.getText().toString();
                ownerStoreName = mStoreName.getText().toString();
                if (ownerName.equals("") || ownerPhone.equals("") || ownerCity.equals("") || ownerStoreType.equals("") || ownerStoreName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields with the (Must) Keyword", Toast.LENGTH_SHORT).show();
                } else {
                    if (!located) {
                        Toast.makeText(MerchantActivity.this, "Please Click on locate me to be locate your current address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (location == null) {
                        Toast.makeText(MerchantActivity.this, "There is an error on your location, please click on Locate Me to locate you current location", Toast.LENGTH_SHORT).show();
                    }
                    JSONObject merchantMsg = new JSONObject();
                    try {
                        merchantMsg.put("Msg","merchant_reg");
                        merchantMsg.put("name",ownerName);
                        merchantMsg.put("phone",ownerPhone);
                        merchantMsg.put("city",ownerCity);
                        merchantMsg.put("storetype",ownerStoreType);
                        merchantMsg.put("storename",ownerStoreName);
                        merchantMsg.put("long",location.getLongitude());
                        merchantMsg.put("lat",location.getLatitude());
                        serverService.sendMessage(merchantMsg.toString());
                        Toast.makeText(MerchantActivity.this, "You have registered successfully, and you shall be contacted soon", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgress(1);
                final FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(MerchantActivity.this);
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsStatus) {
                    Toast.makeText(MerchantActivity.this, "Please enable your gps to be able to locate your location", Toast.LENGTH_SHORT).show();
                    locateMe.setProgress(-1);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(MerchantActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MerchantActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MerchantActivity.this, "Location permission is blocked, please allow the application to use GPS", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MerchantActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 55);
                    locateMe.setProgress(-1);
                    return;
                }
                locateMe.setProgress(50);
                locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if(location == null) {
                            Log.d("Location", "Location is null");
                            return;
                        }

                        MerchantActivity.this.location = location;
                        located = true;
                        locateMe.setProgress(100);
                        Toast.makeText(MerchantActivity.this, "Make sure that we have your current location correctly, in case it is not try renabling your gps or moving around.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        locateMe.setProgress(-1);
                    }
                });

            }

        });

    }



    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(MerchantActivity.this);
            serverService.setMessage(new HandleMessage());
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(serverService != null) {
            serverService.setMessage(new HandleMessage());
        }

    }


    public class HandleMessage implements Runnable, ServerMessage {

        String message;
        @Override
        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                JSONObject msg = new JSONObject(message);
                if(msg.getString("Msg").equals("user_created")) {
                    Toast.makeText(getApplicationContext(), "Your account has been created", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else if(msg.getString("Msg").equals("user_exists")){
                    Toast.makeText(getApplicationContext(), "Username already chosen, please choose another one", Toast.LENGTH_SHORT).show();
                }
                System.out.println(msg.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
