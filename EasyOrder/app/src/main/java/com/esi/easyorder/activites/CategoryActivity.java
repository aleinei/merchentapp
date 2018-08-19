package com.esi.easyorder.activites;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.esi.easyorder.Adapters.CategoryAdapter;
import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.services.ServerService;

public class CategoryActivity extends AppCompatActivity {
    ServerService serverService;
    boolean mBound = false;
    MenuData menuData;
    GridView gridView;
    CategoryAdapter gridAdapter;
    int sectionId;
    int categoryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        gridView = findViewById(R.id.categoryGrid);
        String menu = getIntent().getStringExtra("menuData");
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
        if(menu != null) {
            menuData.deserialize(menu);
            sectionId = section_id;
            categoryId = category_id;
            setTitle(menuData.Sections.get(sectionId).categories.get(categoryId).name);
            LoadItems();
        }
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


    private void LoadItems() {
        gridAdapter = new CategoryAdapter(this, menuData, sectionId, categoryId);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent itemActivity = new Intent(CategoryActivity.this, ItemActivity.class);
                itemActivity.putExtra("item", menuData.Sections.get(sectionId).categories.get(categoryId).items.get(i).toObject().toString());
                startActivity(itemActivity);
            }
        });
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
}
