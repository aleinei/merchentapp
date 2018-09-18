package com.esi.easyorder.activites;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.iml.ActionProcessButton;
import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Item;
import com.esi.easyorder.Order;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.User;
import com.esi.easyorder.services.ServerService;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    EditText buildingText;
    EditText floorApt;
    EditText apt;
    FancyButton registerBtn;
    ActionProcessButton l; // locate location
    boolean locationLocated;
    Location location;
    boolean authinticated;
    String mVerificationId;
    FirebaseAuth mAuth;
    String name, pass, phoneNumber, emailAddress, address_1, building, floor, aprt;

    boolean verifyCancelled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        phone = findViewById(R.id.phoneField);
        email = findViewById(R.id.emailField);
        address1 = findViewById(R.id.address1Field);
        buildingText = findViewById(R.id.buildingField);
        floorApt = findViewById(R.id.floorField);
        apt = findViewById(R.id.aptField);
        String phoneText = getIntent().getStringExtra("phone");
        if(phoneText != null) {
            phone.setText(phoneText);
        }
       // address2 = find ViewById(R.id.address2Field);
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
        setTitle(getString(R.string.registering));

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, ServerService.class);
        bindService(service, mConnection, BIND_AUTO_CREATE);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = username.getText().toString();
                pass = password.getText().toString();
                String passwordConfirm = ((EditText) findViewById(R.id.passwordFieldConfirm)).getText().toString();
                phoneNumber = phone.getText().toString();
                emailAddress = email.getText().toString();
                address_1 = address1.getText().toString();
                building = RegisterActivity.this.buildingText.getText().toString();
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

                    if(phone.length() != 11) {
                        Toast.makeText(serverService, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
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
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 55);
                    l.setProgress(-1);
                    return;
                }
                l.setProgress(50);
                locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        if(location == null) {
                            Log.d("Location", "Location is null");
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String uri = String.format(Locale.ENGLISH, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=AIzaSyCRd8xi9kaak4xo7EuqkQLLQhjaaaNbxac", location.getLatitude(), location.getLongitude());
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url(uri).build();
                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                    String fullString = response.body().string();
                                    JSONObject object = new JSONObject(fullString);
                                    Log.d("JSON Recieved", fullString);
                                    JSONArray results = object.getJSONArray("results");
                                    final String address = results.getJSONObject(0).getString("formatted_address");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            address1.setText(address);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        RegisterActivity.this.location = location;
                        locationLocated = true;
                        l.setProgress(100);
                        Toast.makeText(RegisterActivity.this, "Make sure that we have your current location correctly, in case it is not try renabling your gps or moving around.", Toast.LENGTH_LONG).show();
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
                    SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    User user = new User();
                    user.ID = msg.getInt("id");
                    Log.d("REGISTER", user.ID + " ");
                    user.username = name;
                    Toast.makeText(RegisterActivity.this, getString(R.string.welcome,  user.username), Toast.LENGTH_SHORT).show();
                    user.Address = address_1;
                    user.Telephone = phoneNumber;
                    user.Email = emailAddress;
                    user.location.setLatitude(location.getLatitude());
                    user.location.setLongitude(location.getLongitude());
                    user.Password = pass;
                    ed.putString("user", user.toObject().toString());
                    ed.putBoolean("logged", true);
                    ed.apply();
                    Intent login = new Intent(RegisterActivity.this, MenuActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    finish();
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
