package com.esi.easyorder.Fragments;


import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esi.easyorder.Adapters.ShopTypeAdapter;
import com.esi.easyorder.Constants;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.Shop;
import com.esi.easyorder.ShopsSection;
import com.esi.easyorder.User;
import com.esi.easyorder.activites.CartActivity;
import com.esi.easyorder.activites.SettingsActivity;
import com.esi.easyorder.activites.ShopTypeActivity;
import com.esi.easyorder.services.ServerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopTypeFragment extends android.support.v4.app.Fragment {

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
    boolean loadProfile = false;

    public ShopTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_shop_type, container, false);
        recyclerView = view.findViewById(R.id.shoptyperecy);


        //shtoptype code starts here
        String userString = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("user", "");
        if(!userString.equals(""))
        {
            user = new User();
            user.Deseralize(userString);
        }
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        language = pref.getString("Language","en");

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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

        return view;
    }
    boolean imageIsExist(JSONObject shop){

        return false;
    }
    public void loadShops(JSONObject msg ) {
       try {
           JSONArray shops = msg.getJSONArray("shops");
           for(int i = 0; i < shops.length(); i++) {
               JSONObject shopObj = shops.getJSONObject(i);
               int type = shopObj.getInt("type");
               double lat = shopObj.getDouble("lat");
               double longt = shopObj.getDouble("long");
               Shop shop = new Shop(shopObj.getString("name"), shopObj.getString("name_ar"), shopObj.getString("dbName"), R.drawable.vegetables, shopObj.getBoolean("active"), shopObj.getString("address"), shopObj.getString("phone"), lat, longt);
               if(shop.getIsActive()) {

                   float[] distances = new float[3];
                   try {
                       Location.distanceBetween(lat, longt, user.location.getLatitude(), user.location.getLongitude(), distances);
                       float distance = distances[0] / 1000;
                   } catch (Exception e) {
                       e.printStackTrace();
                   }

                   /*if(distance > 50.0) {
                       continue;
                   }*/
               }
               //String img="http://185.181.10.83/Pictures/Merchants"+shop.getName();

               if(type == Constants.TYPE_VEG) {
                   if(veg.containShop(shop.getDbName())) {
                       return;
                   }
                   shop.setImage(R.drawable.vegetables);
                   veg.shops.add(shop);
               } else if(type == Constants.TYPE_SUPERMARKET){
                   if(markets.containShop(shop.getDbName())) {
                       return;
                   }
                   shop.setImage(R.drawable.markets);
                   markets.shops.add(shop);
               } else if(type == Constants.TYPE_REST) {
                   if(rest.containShop(shop.getDbName())) {
                       return;
                   }
                   shop.setImage(R.drawable.resturant);
                   rest.shops.add(shop);
               } else {
                   if(clothes.containShop(shop.getDbName())) {
                       return;
                   }
                   shop.setImage(R.drawable.clothes_shop);
                   clothes.shops.add(shop);
               }

           }
           loadSections();
       } catch (JSONException e) {
           e.printStackTrace();
       }
    }
    public void loadSections() {
        sections.add(markets);
        sections.add(veg);
        sections.add(rest);
        sections.add(clothes);
        shopTypeAdapter = new ShopTypeAdapter(getContext(), sections);
        recyclerView.setAdapter(shopTypeAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!mBound) {
            Intent service = new Intent(getContext(), ServerService.class);
            //getActivity().bindService(service, mConnection, BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBound) {
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(getActivity());
            serverService.setMessage(new HandleMessage());


            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

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
    } // handlemessage ends here


    public boolean onCreateOptionsMenu(Menu menu) {
        boolean isAdmin = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("isAdmin", false);
        if(isAdmin) {
            getActivity().getMenuInflater().inflate(R.menu.admin_menu, menu);
            return true;
        }
        getActivity().getMenuInflater().inflate(R.menu.a_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.shoppingCart:
                Intent cartActivity = new Intent(getContext(), CartActivity.class);
                startActivity(cartActivity);
                return true;
            case R.id.adminSettings:
                boolean isAdmin = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("isAdmin", false);
                if(isAdmin) {
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent);
                }
        }
        return false;
    }

    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "ar");

        attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }
}
