package com.esi.easyorder.activites;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.esi.easyorder.Adapters.ShopsAdapter;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.R;
import com.esi.easyorder.ShopsSection;
import com.esi.easyorder.User;

public class ShopsActivity extends AppCompatActivity {

    final static String TAG = ShopsActivity.class.getSimpleName();
    RecyclerView shopsRecycler;
    LinearLayoutManager manager;
    SharedPreferences pref;
    String language;
    Toolbar customBar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        customBar = findViewById(R.id.customActionbar);
        setSupportActionBar(customBar);
        getSupportActionBar().setTitle(getString(R.string.shops));
        language = pref.getString("Language","ar");
        String shop  = getIntent().getStringExtra("shop");
        ShopsSection shopsSection = new ShopsSection();
        shopsSection.toClass(shop);
        shopsRecycler = findViewById(R.id.shops);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        final String userString = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
        if(!userString.equals(""))
        {
            user = new User();
            user.Deseralize(userString);
        }
        ShopsAdapter adapter = new ShopsAdapter(this, shopsSection.shops, user);
        shopsRecycler.setLayoutManager(manager);
        shopsRecycler.setAdapter(adapter);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "ar");

        super.attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }
}
