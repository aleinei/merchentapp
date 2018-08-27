package com.esi.easyorder.activites;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Adapters.CategoryAdapter;
import com.esi.easyorder.Item;
import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.services.ServerService;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    ServerService serverService;
    boolean mBound = false;
    MenuData menuData;
    RecyclerView gridView;
    GridLayoutManager layoutManager;
    CategoryAdapter gridAdapter;
    int sectionId;
    int categoryId;
    FloatingActionButton cartConfirm;
    ArrayList<Item> cartItems;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        gridView = findViewById(R.id.categoryGrid);
        layoutManager = new GridLayoutManager(this, 2);
        gridView.setLayoutManager(layoutManager);
        cartConfirm = findViewById(R.id.confirmCart);
        cartConfirm.setVisibility(View.GONE);
        menu = getIntent().getStringExtra("menuData");
        menuData = new MenuData();
        int section_id = getIntent().getIntExtra("sectionId", 0);
        int category_id = getIntent().getIntExtra("categoryId", 0);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getSharedPreferences("global", 0);
        editor = pref.edit();
        if(menu != null) {
            menuData.deserialize(menu);
            sectionId = section_id;
            categoryId = category_id;
            setTitle(menuData.Sections.get(sectionId).categories.get(categoryId).name);
        }
        cartItems = new ArrayList<>();

        cartConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartItems.size() > 0) {
                    for(Item item : cartItems) {
                        String cart = pref.getString("cart", null);
                        Log.d("Double is " , item.qty + "");
                        Log.d("Stored is ", item.qty + "");
                        if(cart != null) {
                            ActiveCart activeCart = new ActiveCart();
                            activeCart.deserialize(cart);
                            activeCart.addItem(item);
                            editor.remove("cart");
                            editor.putString("cart", activeCart.toObject().toString());
                        } else {
                            ActiveCart activeCart = new ActiveCart();
                            activeCart.addItem(item);
                            editor.putString("cart", activeCart.toObject().toString());
                        }
                        editor.apply();
                    }
                    cartItems.clear();
                    cartConfirm.setVisibility(View.GONE);
                    startActivity(new Intent(CategoryActivity.this, CartActivity.class));
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(menu != null) {
            LoadItems();
        }
    }

    private void LoadItems() {
        gridAdapter = new CategoryAdapter(this, menuData, sectionId, categoryId);
        gridView.setAdapter(gridAdapter);
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
            serverService = ((ServerService.Binder) iBinder).getSevice();
            serverService.ConnectServer(CategoryActivity.this);
            serverService.setMessage(new HandleMessage());
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private class HandleMessage implements ServerMessage, Runnable {
        @Override
        public void setMessage(String message) {

        }

        @Override
        public void run() {

        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public void addCartItem(Item item) {
        for(Item Item : cartItems) {
            if(item.id == Item.id) {
                Item.qty = item.qty;
                return;
            }
        }

        cartItems.add(item);
        if(cartConfirm.getVisibility() == View.GONE) cartConfirm.setVisibility(View.VISIBLE);
    }

    public void removeCartItem(Item item) {
        for(Item Item : cartItems) {
            if(item.id == Item.id) {
                if(item.qty == 0) {
                    cartItems.remove(Item);
                    if(cartItems.size() == 0) cartConfirm.setVisibility(View.GONE);
                }
                else Item.qty = item.qty;
                return;
            }
        }

    }
}
