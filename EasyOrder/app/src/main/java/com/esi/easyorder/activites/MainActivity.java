package com.esi.easyorder.activites;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Item;
import com.esi.easyorder.Order;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.User;
import com.esi.easyorder.services.ServerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ServerService serverService;
    boolean mBound = false;
    boolean loggedInPressed = false;
    Button b; // login
    Button r; // register
    EditText usernameText;
    EditText passwordText;
    CheckBox rememberMe;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = findViewById(R.id.signin);
        r = findViewById(R.id.register);
        usernameText = findViewById(R.id.userName);
        passwordText = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMe);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        setTitle("Login");
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().remove("isAdmin").apply();
        user = new User();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this, ServerService.class);
        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loggedInPressed)
                    return;
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "You need to enter your username and password", Toast.LENGTH_SHORT).show();
                } else if (username.equals("admin") && password.equals("admin")) {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isAdmin", true).apply();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    JSONObject message = new JSONObject();
                    try {
                        message.put("Msg", "user_verify");
                        message.put("username", username);
                        message.put("password", password);
                        serverService.sendMessage(message.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                loggedInPressed = true;
            }
        });

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerAcitvity = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerAcitvity);
            }
        });
        boolean isLogged = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("logged", false);
        if (isLogged) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove("user").apply();
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(MainActivity.this);
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
                if(msg.getString("Msg").equals("user_not_exist")) {
                    Toast.makeText(MainActivity.this, "User was not found, please make sure you have one , or create a new one", Toast.LENGTH_SHORT).show();
                    passwordText.setText("");
                    loggedInPressed = false;
                } else if(msg.getString("Msg").equals("user_verified")) {
                    Toast.makeText(MainActivity.this, "Welcome " + usernameText.getText().toString(), Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    user.ID = msg.getInt("Id");
                    user.username = usernameText.getText().toString();
                    user.Address = msg.getString("address");
                    user.Telephone = msg.getString("tele");
                    user.Email = msg.getString("email");
                    user.location.setLatitude(msg.getDouble("lat"));
                    user.location.setLongitude(msg.getDouble("long"));
                    user.Password = msg.getString("pass");
                    JSONArray orders = msg.getJSONArray("orders");
                    ArrayList<Order> ordersList = new ArrayList<>();
                    for(int i = 0; i < orders.length(); i++)
                    {
                        JSONObject orderObject = orders.getJSONObject(i);
                        Order order = new Order();
                        order.ID = orderObject.getInt("id");
                        order.viewd = orderObject.getBoolean("viewed");
                        order.delivered = orderObject.getBoolean("delievered");
                        order.OrderAddress = user.Address;
                        JSONArray items = orderObject.getJSONArray("items");
                        ActiveCart cart = new ActiveCart();
                        for(int y = 0; y < items.length(); y++)
                        {
                            JSONObject item = items.getJSONObject(y);
                            String name = item.getString("name");
                            double price = item.getDouble("price");
                            double qty = item.getDouble("qty");
                            Item it = new Item(name, price, -1, "");
                            it.qty = Math.abs(qty);
                            cart.addItem(it);
                            Log.d("Loaded item", "Loaded");
                        }
                        order.cartOrder = cart;
                        ordersList.add(order);
                    }
                    user.Orders = ordersList;
                    ed.putString("user", user.toObject().toString());
                    ed.apply();
                    Intent login = new Intent(MainActivity.this, MenuActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if(rememberMe.isChecked()) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        editor.putBoolean("logged", true);
                        editor.apply();
                    }
                    startActivity(login);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
