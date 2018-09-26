package com.esi.easyorder.activites;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.esi.easyorder.Adapters.ShopTypeAdapter;
import com.esi.easyorder.Constants;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.Shop;
import com.esi.easyorder.ShopsSection;
import com.esi.easyorder.User;
import com.esi.easyorder.services.ServerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ShopTypeActivity extends AppCompatActivity {

    SharedPreferences pref;
    String language;
    final static String TAG = ShopTypeActivity.class.getSimpleName();
    RecyclerView recyclerView;
    ShopTypeAdapter shopTypeAdapter;
    ServerService serverService;
    public boolean mBound;
    ArrayList<ShopsSection> sections = new ArrayList<>();
    ShopsSection veg = new ShopsSection();
    ShopsSection rest = new ShopsSection();
    ShopsSection clothes = new ShopsSection();
    ShopsSection markets = new ShopsSection();
    Toolbar customBar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoptype);
        recyclerView = findViewById(R.id.shoptyperecy);
        customBar = findViewById(R.id.customActionbar);
        setSupportActionBar(customBar);
        getSupportActionBar().setTitle(getString(R.string.types));
        String userString = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        if(!userString.equals(""))
        {
            user = new User();
            user.Deseralize(userString);
        }
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString("Language","ar");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        veg.name = getString(R.string.vegetables);
        veg.image = R.drawable.vegetables;

        rest.name = getString(R.string.restaurants);
        rest.image = R.drawable.resturant;

        clothes.name = getString(R.string.clothes);
        clothes.image = R.drawable.clothes_shop;

        markets.name = getString(R.string.markets);
        markets.image = R.drawable.markets;
    }


    public void loadSections() {
        sections.add(markets);
        sections.add(veg);
        sections.add(rest);
        sections.add(clothes);
        shopTypeAdapter = new ShopTypeAdapter(this, sections);
        recyclerView.setAdapter(shopTypeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mBound) {
            Intent service = new Intent(this, ServerService.class);
            bindService(service, mConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(ShopTypeActivity.this);
            serverService.setMessage(new ShopTypeActivity.HandleMessage());
            JSONObject msg = new JSONObject();
            try {
                msg.put("Msg", "get_shops");
                serverService.sendMessage(msg.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if(serverService != null) {
            serverService.setMessage(new ShopTypeActivity.HandleMessage());
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
                if(msg.getString("Msg").equals("registered_shops")) {
                    Log.d(TAG, "getting shops");
                    Log.d(TAG, msg.toString());

                    JSONArray shops = msg.getJSONArray("shops");
                    for(int i = 0; i < shops.length(); i++) {
                        JSONObject shopObj = shops.getJSONObject(i);
                        int type = shopObj.getInt("type");
                        Shop shop = new Shop(shopObj.getString("name"), shopObj.getString("name_ar"), shopObj.getString("dbName"), R.drawable.vegetables, shopObj.getBoolean("active"), shopObj.getString("address"), shopObj.getString("phone"));
                        if(shop.getIsActive()) {
                            double lat = shopObj.getDouble("lat");
                            double longt = shopObj.getDouble("long");
                            float[] distances = new float[3];
                            Location.distanceBetween(lat, longt, user.location.getLatitude(), user.location.getLongitude(), distances);
                            float distance = distances[0] / 1000;
                            if(distance > 10.0) {
                                continue;
                            }
                        }
                        if(type == Constants.TYPE_VEG) {
                            shop.setImage(R.drawable.vegetables);
                            veg.shops.add(shop);
                        } else if(type == Constants.TYPE_SUPERMARKET){
                            shop.setImage(R.drawable.markets);
                            markets.shops.add(shop);
                        } else if(type == Constants.TYPE_REST) {
                            shop.setImage(R.drawable.resturant);
                            rest.shops.add(shop);
                        } else {
                            shop.setImage(R.drawable.clothes_shop);
                            clothes.shops.add(shop);
                        }
                    }
                    loadSections();
                    //shop name
                    //shop dbName
                    //shop type
                    //Shop shop = new Shop(name, dbName, null)
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean isAdmin = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isAdmin", false);
        if(isAdmin) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
            return true;
        }
        getMenuInflater().inflate(R.menu.a_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.shoppingCart:
                Intent cartActivity = new Intent(this, CartActivity.class);
                startActivity(cartActivity);
                return true;
            case R.id.adminSettings:
                boolean isAdmin = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isAdmin", false);
                if(isAdmin) {
                    Intent intent = new Intent(ShopTypeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
        }
        return false;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "ar");

        super.attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }
}
