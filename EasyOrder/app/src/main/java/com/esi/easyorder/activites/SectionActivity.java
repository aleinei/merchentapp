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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esi.easyorder.Adapters.SectionAdapter;
import com.esi.easyorder.Adapters.SectionReycleAdapter;
import com.esi.easyorder.MenuData;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.R;
import com.esi.easyorder.Section;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.services.ServerService;

public class SectionActivity extends AppCompatActivity {
    SharedPreferences pref;
    Section menuData;
    GridView gridView;
    LinearLayout gridViewLayout;
    SectionAdapter adapter;
    ServerService serverService;
    boolean mBound = false;
    String language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString("Language","en");
        String menu = getIntent().getStringExtra("menuData");
        gridViewLayout = findViewById(R.id.sectionGrid);
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
            menuData = new Section();
            menuData.deseralize(menu);
            setTitle(menuData.name);
            loadCategories();
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
            serverService.ConnectServer(SectionActivity.this);
            serverService.setMessage(new HandleMessage());
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serverService = null;
            mBound = true;
        }
    };

    private void loadCategories() {
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2,1);
        GridLayoutManager lm2 = new GridLayoutManager(this, 8);
        lm2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(menuData.categories.size() <= 4)
                    return 8;
                if(position % 5 == 0)
                    return 8;
                else
                    return 4;
            }
        });
        recyclerView.setLayoutManager(lm2);
        SectionReycleAdapter adapter = new SectionReycleAdapter(this, menuData);
        recyclerView.setAdapter(adapter);
        gridViewLayout.addView(recyclerView);
    }

    private class HandleMessage implements ServerMessage, Runnable {
        @Override
        public void setMessage(String message) {

        }

        @Override
        public void run() {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "en");

        super.attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }

}
