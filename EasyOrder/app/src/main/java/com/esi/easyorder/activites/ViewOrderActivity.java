package com.esi.easyorder.activites;

import android.support.v4.app.Fragment;
import com.esi.easyorder.Fragments.ShopTypeFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.esi.easyorder.Adapters.CartAdapter;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.Order;
import com.esi.easyorder.R;

public class ViewOrderActivity extends AppCompatActivity {

    TextView orderName;
    TextView orderPrice;
    EditText orderAddress;
    GridView gridView;
    String language;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        language = pref.getString("Language","ar");
        orderName = findViewById(R.id.orderName);
        orderAddress = findViewById(R.id.deliveryAddress);
        gridView = findViewById(R.id.cartGridView);
        orderPrice = findViewById(R.id.cartCost);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.orderdetails));
        String order = getIntent().getStringExtra("order");
        if(order != null)
        {
            Order order1 = new Order();
            order1.Deseralize(order);
            orderName.setText(getString(R.string.ordernumber, order1.ID) );
            orderAddress.setText(order1.OrderAddress);
            CartAdapter adapter = new CartAdapter(this, order1.cartOrder);
            gridView.setAdapter(adapter);
            orderPrice.setText(String.valueOf(order1.cartOrder.cost));
        }
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "ar");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }
}
