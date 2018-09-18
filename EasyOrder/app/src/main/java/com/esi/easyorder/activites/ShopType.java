package com.esi.easyorder.activites;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.esi.easyorder.R;

import org.w3c.dom.Text;

public class ShopType extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    TextView storetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoptype);

        coordinatorLayout = findViewById(R.id.shoptypelayout);
        storetype = findViewById(R.id.storetype);
        coordinatorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopType.this,Shops.class);
                startActivity(intent);
            }
        });
    }

}
