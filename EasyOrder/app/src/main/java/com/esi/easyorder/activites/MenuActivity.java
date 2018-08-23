package com.esi.easyorder.activites;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esi.easyorder.Adapters.MenuReycleAdapter;
import com.esi.easyorder.Category;
import com.esi.easyorder.ExtraItem;
import com.esi.easyorder.Fragments.MenuFragment;
import com.esi.easyorder.Fragments.OrdersFragment;
import com.esi.easyorder.Fragments.ProfileFragment;
import com.esi.easyorder.Item;
import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.esi.easyorder.Section;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.User;
import com.esi.easyorder.services.ServerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MenuActivity extends AppCompatActivity {

    public ServerService serverService;
    boolean mBound = false;
    public boolean menuLoaded = false;
    public MenuData menuData;
    LinearLayout gridViewLayout;
    LinearLayout loadingStart;
    RecyclerView recyclerView;
    String serverIP;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView NavView;
    Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menuData = new MenuData();
        serverIP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("IPAddress", "192.168.1.5");
        toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggler = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok, android.R.string.no);;
        drawerLayout.addDrawerListener(toggler);
        toggler.syncState();
        NavView = findViewById(R.id.navView);
        NavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId())
                {
                    case R.id.Home:
                        LoadHome();
                        setTitle("Menu");
                        break;
                    case R.id.userOrders:
                        LoadOrders();
                        setTitle("Your Orders");
                        break;
                    case R.id.userSettings:
                        currentFragment = new ProfileFragment();
                        ((ProfileFragment)currentFragment).menuActivity = MenuActivity.this;
                        getFragmentManager().beginTransaction().replace(R.id.frameContent, currentFragment).commit();
                        break;
                    case R.id.userLogout:
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        editor.remove("logged");
                        editor.remove("Id");
                        editor.apply();
                        Intent signin = new Intent(MenuActivity.this, MainActivity.class);
                        signin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(signin);
                        return true;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        TextView userName = NavView.getHeaderView(0).findViewById(R.id.userInfoName);
        TextView email = NavView.getHeaderView(0).findViewById(R.id.userInfoEmail);
        String user = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        if(!user.equals(""))
        {
            User currentUser = new User();
            currentUser.Deseralize(user);
            userName.setText(currentUser.username);
            email.setText(currentUser.Email);
        }
        LoadHome();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent clientService = new Intent(this, ServerService.class);
        bindService(clientService, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serverService = ((ServerService.Binder)iBinder).getSevice();
            serverService.ConnectServer(MenuActivity.this);
            serverService.setMessage(new HandleMessage());
            mBound = true;
            LoadMenudata();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serverService = null;
            mBound = true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public void LoadMenudata() {
        JSONObject message = new JSONObject();
        try {
            message.put("Msg", "all_sections");
            serverService.sendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                    Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
        }
        return false;
    }

    public void LoadMenu() {
      recyclerView = new RecyclerView(this);
      recyclerView.setHasFixedSize(true);
      StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2,1);
        GridLayoutManager lm2 = new GridLayoutManager(this, 8);
      lm2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
          @Override
          public int getSpanSize(int position) {
              if(menuData.Sections.size() <= 4)
                  return 8;
              if(position % 5 == 0)
                  return 8;
              else
                  return 4;
          }
      });
      recyclerView.setLayoutManager(lm2);
      MenuReycleAdapter adapter = new MenuReycleAdapter(this, menuData);
      recyclerView.setAdapter(adapter);
      gridViewLayout.addView(recyclerView);
      loadingStart.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(serverService != null) {
            serverService.setMessage(new HandleMessage());
        }
    }

    class HandleMessage implements Runnable, ServerMessage {

        String message;
        @Override
        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                JSONArray objects = new JSONArray(message);
                String msg = objects.getJSONObject(0).getString("Msg");
                String extraInfo = objects.getJSONObject(0).getString("info");
                if (msg.equals("all_sections")) {
                    if (extraInfo != null && extraInfo.equals("no_sections")) {

                    } else {
                        String[] SectionNames = new String[objects.length() -1] ;
                        for (int i = 1; i < objects.length(); i++) {
                            JSONObject object = objects.getJSONObject(i);
                            SectionNames[i - 1] = object.getString("section_name");
                            StringBuilder s = new StringBuilder(100);
                            s.append("http://" + serverIP + "/Pictures/Menus/Guest%20Menu/Sections/");
                            s.append(Uri.encode(object.getString("section_name")));
                            s.append(".jpg");
                            menuData.Sections.add(new Section(object.getString("section_name"), object.getInt("section_id"), s.toString()));
                            System.out.println("New section " +object.getString("section_name"));
                        }
                        JSONObject obj = new JSONObject();
                        obj.put("Msg", "section_categories");
                        serverService.sendMessage(obj.toString());
                    }
                } else if (msg.equals("section_categories")) {
                    if (extraInfo != null && !extraInfo.equals("null")) {
                        for (int y = 1; y < objects.length(); y++) {
                            JSONObject jsonObject = objects.getJSONObject(y);
                            int section_id = jsonObject.getInt("section_id");
                            for (int i = 0; i < menuData.Sections.size(); i++) {
                                if (menuData.Sections.get(i).Id == section_id) {
                                    StringBuilder s = new StringBuilder(100);
                                    s.append("http://" + serverIP + "/Pictures/Menus/Guest%20Menu/Categories/");
                                    s.append(Uri.encode(jsonObject.getString("Name")));
                                    s.append(".jpg");
                                    Category category = new Category(jsonObject.getString("Name"), jsonObject.getInt("Id"), menuData.Sections.get(i).name, s.toString());
                                    menuData.Sections.get(i).categories.add(category);
                                }
                            }
                        }
                        JSONObject obj = new JSONObject();
                        obj.put("Msg", "category_items");
                        serverService.sendMessage(obj.toString());

                    }
                } else if (msg.equals("category_items")) {
                    JSONArray items = objects.getJSONArray(1);
                    for (int x = 0; x < items.length(); x++) {
                        JSONObject item = items.getJSONObject(x);
                        int section_id = item.getInt("section_id");
                        int category_id = item.getInt("category_id");
                        for (int i = 0; i <  menuData.Sections.size(); i++) {
                            if ( menuData.Sections.get(i).Id == section_id) {
                                for (int y = 0; y <  menuData.Sections.get(i).categories.size(); y++) {
                                    if ( menuData.Sections.get(i).categories.get(y).Id == category_id) {
                                        StringBuilder s = new StringBuilder(100);
                                        s.append("http://" + serverIP + "/Pictures/Menus/Guest%20Menu/Items/");
                                        s.append(Uri.encode(item.getString("Name")));
                                        s.append(".jpg");
                                        Item it = new Item(item.getString("Name"), item.getDouble("Price"), item.getInt("Id"), s.toString());
                                        it.maxAddableItems = item.getInt("maxChild");
                                        try {
                                            it.Source = item.getString("source");
                                        } catch (JSONException eex) {
                                            it.Source = " ";
                                        }
                                        it.unit = item.getInt("unit");
                                        menuData.Sections.get(i).categories.get(y).items.add(it);
                                    }
                                }
                            }
                        }
                    }
                    JSONObject o = new JSONObject();
                    o.put("Msg", "extra_items");
                    serverService.sendMessage(o.toString());
                } else if(msg.equals("extra_items")) {
                 //   Toast.makeText(getApplicationContext(), "Loading Extra items", Toast.LENGTH_SHORT).show();
                    JSONArray items = objects.getJSONArray(1);
                    Log.d("Items", items.length() + "");
                    for(int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        int itemID = item.getInt("itemId");
                        for(int x = 0; x < menuData.Sections.size(); x++) {
                            for(int y = 0; y < menuData.Sections.get(x).categories.size(); y++) {
                                for(int e = 0; e < menuData.Sections.get(x).categories.get(y).items.size(); e++) {
                                    Item t = menuData.Sections.get(x).categories.get(y).items.get(e);
                                    if(t.id == itemID) {
                                    //    Toast.makeText(getApplicationContext(), t.itemName, Toast.LENGTH_SHORT).show();
                                        ExtraItem et = new ExtraItem();
                                        et.ID = item.getInt("extraId");
                                        et.name = item.getString("extraName");
                                        et.price = item.getDouble("extraPrice");
                                        et.Qty = item.getDouble("qty");
                                        int affectsPrice = item.getInt("effectsPrice");
                                        if(affectsPrice == 0)
                                            et.AddToPrice = false;
                                        else
                                            et.AddToPrice = true;
                                        t.ExtraItems.add(et);
                                    }
                                }
                            }
                        }
                   //     Toast.makeText(getApplicationContext(), "DONE LOADING ALL", Toast.LENGTH_SHORT).show();
                    }
                    JSONObject o = new JSONObject();
                    o.put("Msg", "choose_items");
                    serverService.sendMessage(o.toString());
                } else if(msg.equals("choose_items")) {
                    JSONArray items = objects.getJSONArray(1);
                    Log.d("Items", items.length() + "");
                    for(int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        int itemID = item.getInt("itemId");
                        for(int x = 0; x < menuData.Sections.size(); x++) {
                            for(int y = 0; y < menuData.Sections.get(x).categories.size(); y++) {
                                for(int e = 0; e < menuData.Sections.get(x).categories.get(y).items.size(); e++) {
                                    Item t = menuData.Sections.get(x).categories.get(y).items.get(e);
                                    if(t.id == itemID) {
                                        //    Toast.makeText(getApplicationContext(), t.itemName, Toast.LENGTH_SHORT).show();
                                        ExtraItem et = new ExtraItem();
                                        et.ID = item.getInt("extraId");
                                        et.name = item.getString("extraName");
                                        et.price = item.getDouble("extraPrice");
                                        et.Qty = item.getDouble("qty");
                                        int affectsPrice = item.getInt("effectsPrice");
                                        if(affectsPrice == 0)
                                            et.AddToPrice = false;
                                        else
                                            et.AddToPrice = true;
                                        t.AddableItems.add(et);
                                    }
                                }
                            }
                        }
                        //     Toast.makeText(getApplicationContext(), "DONE LOADING ALL", Toast.LENGTH_SHORT).show();
                    }
                    JSONObject o = new JSONObject();
                    o.put("Msg", "without_items");
                    serverService.sendMessage(o.toString());
                } else if(msg.equals("without_items")) {
                    JSONArray items = objects.getJSONArray(1);
                    Log.d("Items", items.length() + "");
                    for(int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        int itemID = item.getInt("itemId");
                        for(int x = 0; x < menuData.Sections.size(); x++) {
                            for(int y = 0; y < menuData.Sections.get(x).categories.size(); y++) {
                                for(int e = 0; e < menuData.Sections.get(x).categories.get(y).items.size(); e++) {
                                    Item t = menuData.Sections.get(x).categories.get(y).items.get(e);
                                    if(t.id == itemID) {
                                        //    Toast.makeText(getApplicationContext(), t.itemName, Toast.LENGTH_SHORT).show();
                                        ExtraItem et = new ExtraItem();
                                        et.ID = item.getInt("extraId");
                                        et.name = item.getString("extraName");
                                        et.price = item.getDouble("extraPrice");
                                        et.Qty = item.getDouble("qty");
                                        int affectsPrice = item.getInt("effectsPrice");
                                        if(affectsPrice == 0)
                                            et.AddToPrice = false;
                                        else
                                            et.AddToPrice = true;
                                        t.WithoutItems.add(et);
                                        //Toast.makeText(getApplicationContext(), "LOADING " + et.name, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                    menuLoaded = true;
                    if(currentFragment instanceof MenuFragment)
                    {
                        ((MenuFragment)currentFragment).LoadMenu();
                    }
                }
            } catch (JSONException e) {
                try {
                    JSONObject msg = new JSONObject(message);
                    if(msg.getString("Msg").equals("user_update"))
                    {
                        boolean userUpdated = msg.getBoolean("user_updated");
                        String message = msg.getString("message");
                        if(currentFragment instanceof ProfileFragment)
                        {
                            ((ProfileFragment)currentFragment).UserUpdate(userUpdated, message);
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void LoadHome()
    {
        currentFragment = new MenuFragment();
        ((MenuFragment)currentFragment).menuActivity = this;
        getFragmentManager().beginTransaction().replace(R.id.frameContent, currentFragment).commit();
        NavView.getMenu().getItem(0).setChecked(true);
        setTitle("Menu");
    }

    public void LoadOrders()
    {
        currentFragment = new OrdersFragment();
        getFragmentManager().beginTransaction().replace(R.id.frameContent, currentFragment).commit();
        setTitle("Your Orders");
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(NavView))
        {
            drawerLayout.closeDrawers();
            return;
        }
        if(currentFragment instanceof MenuFragment)
            super.onBackPressed();
        else
            LoadHome();
    }
}
