package com.esi.easyorder.activites;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import com.esi.easyorder.R;
import com.esi.easyorder.Section;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.services.ServerService;

public class SectionActivity extends AppCompatActivity {

    MenuData menuData;
    GridView gridView;
    LinearLayout gridViewLayout;
    SectionAdapter adapter;
    ServerService serverService;
    boolean mBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        String menu = getIntent().getStringExtra("menuData");
        int section_id = getIntent().getIntExtra("sectionId", 0);
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
            menuData = new MenuData();
            menuData.deserialize(menu);
            setTitle(menuData.Sections.get(section_id).name);
            loadCategories(section_id);
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

    private void loadCategories(final int sectionId) {
        if(menuData != null && menuData.Sections != null) {
            Section s = menuData.Sections.get(sectionId);
           if(s == null) Toast.makeText(getApplicationContext(), "s is null", Toast.LENGTH_SHORT).show();
            else {
               /* adapter = new SectionAdapter(this, s);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent categoryActivity = new Intent(SectionActivity.this, CategoryActivity.class);
                        categoryActivity.putExtra("menuData", menuData.toString());
                        categoryActivity.putExtra("sectionId", sectionId);
                        categoryActivity.putExtra("categoryId", i);
                        startActivity(categoryActivity);
                    }
                });*/
               RecyclerView recyclerView = new RecyclerView(this);
               recyclerView.setHasFixedSize(true);
               StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2,1);
               GridLayoutManager lm2 = new GridLayoutManager(this, 8);
               lm2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                   @Override
                   public int getSpanSize(int position) {
                       if(menuData.Sections.get(sectionId).categories.size() <= 4)
                           return 8;
                       if(position % 5 == 0)
                           return 8;
                       else
                           return 4;
                   }
               });
               recyclerView.setLayoutManager(lm2);
               SectionReycleAdapter adapter = new SectionReycleAdapter(this, menuData, sectionId);
               recyclerView.setAdapter(adapter);
               gridViewLayout.addView(recyclerView);
                //Toast.makeText(getApplicationContext(), "DONE " + s.name, Toast.LENGTH_SHORT).show();
            }
        }
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
}
