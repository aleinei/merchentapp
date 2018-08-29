package com.esi.easyorder.activites;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Adapters.CartAdapter;
import com.esi.easyorder.Item;
import com.esi.easyorder.Order;
import com.esi.easyorder.R;
import com.esi.easyorder.ServerMessage;
import com.esi.easyorder.User;
import com.esi.easyorder.services.ServerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {

    ServerService serverService;
    boolean mBound = false;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ActiveCart mCart;
    CartAdapter gridAdapter;
    GridView gridView;
    TextView cartCost;
    boolean isEmpty = true;
    boolean orderSent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        pref = getSharedPreferences("global", 0);
        editor = pref.edit();
        String cart = pref.getString("cart", null);
        mCart = new ActiveCart();
        gridView = findViewById(R.id.cartGridView);
        cartCost = findViewById(R.id.cartCost);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(cart != null) {
            mCart.deserialize(cart);
            gridAdapter = new CartAdapter(this, mCart);
            gridView.setAdapter(gridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
                    AlertDialog aDialog;
                    TextView textView = new TextView(CartActivity.this);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText("Are you sure you want to remove this item?");
                    textView.setPadding(0,5,0,0);
                    textView.setTextSize(18);
                    dialog.setTitle("Are you sure you want to remove this item?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gridAdapter.DeleteItem(index);
                            editor.remove("cart");
                            if(gridAdapter.GetCart().Items.size() > 0) {
                                editor.putString("cart", gridAdapter.GetCart().toObject().toString());
                                DecimalFormat df = new DecimalFormat("#.000");
                                cartCost.setText(df.format(gridAdapter.GetCart().cost));

                            } else {
                                findViewById(R.id.noOrders).setVisibility(View.VISIBLE);
                                isEmpty = true;
                            }
                            editor.apply();
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    aDialog = dialog.create();
                    aDialog.show();
                }
            });
            DecimalFormat df = new DecimalFormat("#.000");
            cartCost.setText(df.format(gridAdapter.GetCart().cost));
            isEmpty = false;
        } else {
            findViewById(R.id.noOrders).setVisibility(View.VISIBLE);
        }
        setTitle("سلة المشتريات");
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
            serverService.ConnectServer(CartActivity.this);
            serverService.setMessage(new HandleMessage());
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serverService = null;
            mBound = true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.cartSend:
                if(!isEmpty && !orderSent) {
                    String user = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "");
                    User currentUser;
                    if(!user.equals(""))
                    {
                        currentUser = new User();
                        currentUser.Deseralize(user);
                        final int Id = currentUser.ID;
                        if (Id != -1) {
                            LinearLayout layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(20,5,10,5);
                            TextView option1 = new TextView(this);
                            TextView option2 = new TextView(this);
                            option1.setPadding(5,5,5,10);
                            option1.setTextColor(Color.BLACK);
                            option1.setTextSize(21);

                            option2.setPadding(5,5,5,50);
                            option2.setTextColor(Color.BLACK);
                            option2.setTextSize(21);

                            option1.setText("Delivery");
                            option2.setText("Pickup");
                            layout.addView(option1);
                            layout.addView(option2);
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);

                            builder.setView(layout);
                            builder.setMessage("Checkout");
                            Order order = new Order();
                            order.cartOrder = gridAdapter.cart;
                            order.viewd = false;
                            order.ID = 1;
                            order.OrderAddress = currentUser.Address;
                            currentUser.Orders.add(order);
                            PreferenceManager.getDefaultSharedPreferences(this).edit().remove("user").apply();
                            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user", currentUser.toObject().toString()).apply();
                            final AlertDialog dialog = builder.create();
                            dialog.show();
                            option1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    JSONObject cart = new JSONObject();
                                    try {
                                        cart.put("Msg", "new_order_d");
                                        cart.put("user_id", Id);
                                        cart.put("takeaway", false);
                                        JSONArray items = new JSONArray();
                                        for (Item i : gridAdapter.cart.Items) {
                                            i.qty = Double.valueOf(new DecimalFormat("#.000").format(i.qty));
                                            items.put(i.toObject());
                                        }
                                        cart.put("items", items);
                                        cart.put("cost", gridAdapter.cart.cost);
                                        serverService.sendMessage(cart.toString());
                                        editor.remove("cart");
                                        editor.apply();
                                        onBackPressed();
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            option2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    JSONObject cart = new JSONObject();
                                    try {
                                        cart.put("Msg", "new_order_d");
                                        cart.put("user_id", Id);
                                        cart.put("takeaway", true);
                                        JSONArray items = new JSONArray();
                                        for (Item i : gridAdapter.cart.Items) {
                                            i.qty = Double.valueOf(new DecimalFormat("#.000").format(i.qty));
                                            items.put(i.toObject());
                                        }
                                        cart.put("items", items);
                                        cart.put("cost", gridAdapter.cart.cost);
                                        serverService.sendMessage(cart.toString());
                                        editor.remove("cart");
                                        editor.apply();
                                        onBackPressed();
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Id not read", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "User not read", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    orderSent = true;
                }
                return true;
        }
        return false;
    }

    private class HandleMessage implements ServerMessage, Runnable {

        public String message;
        @Override
        public void setMessage(String message) {
            this.message = message;
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
