package com.esi.easyorder.activites;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.services.ServerService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    ServerService serverService;
    boolean mBound = false;

    EditText username;
    EditText password;
    EditText phone;
    EditText email;
    EditText address1;
    EditText building;
    EditText floorApt;
    EditText apt;
    FancyButton registerBtn;
    ActionProcessButton l; // locate location
    boolean locationLocated;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        phone = findViewById(R.id.phoneField);
        email = findViewById(R.id.emailField);
        address1 = findViewById(R.id.address1Field);
        building = findViewById(R.id.buildingField);
        floorApt = findViewById(R.id.floorField);
        apt = findViewById(R.id.aptField);
       // address2 = findViewById(R.id.address2Field);
        l = findViewById(R.id.getLocation);
        registerBtn = findViewById(R.id.register);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Register");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, ServerService.class);
        bindService(service, mConnection, BIND_AUTO_CREATE);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, pass, phoneNumber, emailAddress, address_1, building, floor, aprt;

                name = username.getText().toString();
                pass = password.getText().toString();
                String passwordConfirm = ((EditText) findViewById(R.id.passwordFieldConfirm)).getText().toString();
                phoneNumber = phone.getText().toString();
                emailAddress = email.getText().toString();
                address_1 = address1.getText().toString();
                building = RegisterActivity.this.building.getText().toString();
                floor = floorApt.getText().toString();
                aprt = apt.getText().toString();
                if(name.equals("") || pass.equals("") || phoneNumber.equals("") || emailAddress.equals("") || address_1.equals("") || floor.equals("") || building.equals("") || aprt.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields with the (Must) Keyword", Toast.LENGTH_SHORT).show();
                } else {
                    if (!pass.equals(passwordConfirm)) {
                        Toast.makeText(RegisterActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(name.contains(" "))
                    {
                        Toast.makeText(RegisterActivity.this, "Username can not contain spaces, please remove it to continue", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!locationLocated)
                    {
                        Toast.makeText(RegisterActivity.this, "Please Click on locate me to be locate your current address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(location == null)
                    {
                        Toast.makeText(RegisterActivity.this, "There is an error on your location, please click on Locate Me to locate you current location", Toast.LENGTH_SHORT).show();
                    }
                    String error = "";
                    if(name.length() < 6 ) {
                        error = "You need to have atleast 6 characters in your username";
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(pass.length() < 6) {
                        error = "You need to have atleast 6 characters in your password";
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject msg = new JSONObject();
                    try {
                        msg.put("Msg", "new_user");
                        msg.put("username", name);
                        msg.put("password", pass);
                        msg.put("phone", phoneNumber);
                        msg.put("email", emailAddress);
                        msg.put("address1", address_1);
                        msg.put("building", building);
                        msg.put("floor", floor);
                        msg.put("apt", aprt);
                        msg.put("lat", location.getLatitude());
                        msg.put("long", location.getLongitude());
                        serverService.sendMessage(msg.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l.setProgress(1);
                final FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(RegisterActivity.this);
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsStatus) {
                    Toast.makeText(RegisterActivity.this, "Please enable your gps to be able to locate your location", Toast.LENGTH_SHORT).show();
                    l.setProgress(-1);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(RegisterActivity.this, "Location permission is blocked, please allow the application to use GPS", Toast.LENGTH_SHORT).show();
                    l.setProgress(-1);
                    return;
                }
                l.setProgress(50);
                locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if(location == null) {

                        }
                        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if(addressList.size() > 0)
                            {
                               /* String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", location.getLatitude(), location.getLongitude());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);*/
                               new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                       String uri = String.format(Locale.ENGLISH, "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%f,%f&destinations=%f,%f", location.getLatitude(), location.getLongitude(), 30.075807f, 31.281116f);
                                       OkHttpClient client = new OkHttpClient();
                                       Request request = new Request.Builder().url(uri).build();
                                       Response response = null;
                                       try {
                                           response = client.newCall(request).execute();
                                           String fullString = response.body().string();
                                           JSONObject object = new JSONObject(fullString);
                                           String time = object.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
                                           Log.d("Time to get there is", time);
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }
                                   }
                               }).start();
                                RegisterActivity.this.location = location;
                                Address address = addressList.get(0);
                                address1.setText(address.getAddressLine(0));
                                locationLocated = true;
                                l.setProgress(100);
                                Toast.makeText(RegisterActivity.this, "Make sure that we have your current location correctly, in case it is not try renabling your gps or moving around.", Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            l.setProgress(-1);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        l.setProgress(-1);
                    }
                });

            }
        });
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(RegisterActivity.this);
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
