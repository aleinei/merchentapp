package com.esi.easyorder.activites;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Adapters.CheckBoxesAdapter;
import com.esi.easyorder.ExtraItem;
import com.esi.easyorder.Item;
import com.esi.easyorder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.polidea.webimageview.WebImageView;

public class ItemActivity extends AppCompatActivity {

    com.github.clans.fab.FloatingActionButton addBtn;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Item mItem;
    Item purchaseItem;
    EditText qty;
    Button extraButton;
    Button addButton;
    Button withoutButton;
    CheckBox[] addingItemsNames;
    CheckBox[] extraItemsNames;
    CheckBox[] withoutItemsNames;
    int addedChild = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        String item = getIntent().getStringExtra("item");
        pref = getSharedPreferences("global", 0);
        editor = pref.edit();
        addBtn = findViewById(R.id.addToOrder);
        qty = findViewById(R.id.itemQty);
        extraButton = findViewById(R.id.btnExtraItems);
        addButton = findViewById(R.id.btnAddItem);
        withoutButton = findViewById(R.id.btnWithouItem);
        Toolbar toolbar = findViewById(R.id.customActionbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(item != null) {
            try {
                JSONObject it = new JSONObject(item);
                String itemName = it.getString("itemName");
                double itemPrice = it.getDouble("itemPrice");
                String name = it.getString("itemName");
                String url = it.getString("imageURL");
                WebImageView image = findViewById(R.id.itemDesc);
                image.setImageURL(url);
                double price = it.getDouble("itemPrice");
                int Id = it.getInt("itemId");
                int maxChild = it.getInt("maxChild");
                String source = it.getString("Source");
                mItem =  new Item(itemName, itemPrice, Id, null);
                mItem.maxAddableItems = maxChild;
                purchaseItem = new Item(itemName, itemPrice, Id, null);
                purchaseItem.ExtraItems.clear();
                purchaseItem.Source = source;
                purchaseItem.unit = it.getInt("unit");
                if(purchaseItem.unit == 1)
                {
                    qty.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else
                {
                    qty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    findViewById(R.id.kiloText).setVisibility(View.VISIBLE);
                }
                boolean hasextra = it.getBoolean("hasextra");
                if(hasextra) {
                    extraButton.setVisibility(View.VISIBLE);
                    JSONArray extraitems = it.getJSONArray("extraitems");
                    for(int i = 0 ; i < extraitems.length(); i++) {
                        JSONObject extraItem = extraitems.getJSONObject(i);
                        ExtraItem e = new ExtraItem();
                        e.ID = extraItem.getInt("id");
                        e.name = extraItem.getString("name");
                        e.AddToPrice = extraItem.getBoolean("addtoprice");
                        e.price = extraItem.getDouble("price");
                        e.Qty = extraItem.getDouble("qty");
                        mItem.ExtraItems.add(e);
                    }
                    boolean hasAdd = it.getBoolean("hasadd");
                    if(hasAdd) {
                        addButton.setVisibility(View.VISIBLE);
                        JSONArray extraItems = it.getJSONArray("addableitems");
                        for(int x = 0; x < extraItems.length(); x++) {
                            JSONObject extraItem = extraItems.getJSONObject(x);
                            ExtraItem e = new ExtraItem();
                            e.name = extraItem.getString("name");
                            e.ID = extraItem.getInt("id");
                            e.AddToPrice = extraItem.getBoolean("addtoprice");
                            e.price = extraItem.getDouble("price");
                            e.Qty = extraItem.getDouble("qty");
                            mItem.AddableItems.add(e);
                        }
                    }
                    boolean haswithout = it.getBoolean("haswithout");
                    if(haswithout) {
                        withoutButton.setVisibility(View.VISIBLE);
                        JSONArray withoutItem = it.getJSONArray("withoutitems");
                        for(int x = 0; x < withoutItem.length(); x++) {
                            JSONObject extraItem = withoutItem.getJSONObject(x);
                            ExtraItem e = new ExtraItem();
                            e.name = extraItem.getString("name");
                            e.ID = extraItem.getInt("id");
                            e.AddToPrice = extraItem.getBoolean("addtoprice");
                            e.price = extraItem.getDouble("price");
                            e.Qty = extraItem.getDouble("qty");
                            mItem.WithoutItems.add(e);
                        }
                    }
                    extraButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder build = new AlertDialog.Builder(ItemActivity.this);
                            ListView listView = new ListView(ItemActivity.this);
                            LinearLayout layout = new LinearLayout(ItemActivity.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(10,10,10,10);
                            extraItemsNames = new CheckBox[mItem.ExtraItems.size()];
                            for(int i = 0; i < mItem.ExtraItems.size(); i++) {
                                CheckBox checkBox = new CheckBox(ItemActivity.this);
                                checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                checkBox.setText(mItem.ExtraItems.get(i).name);
                                if(purchaseItem.ExtraItems.size() > 0) {
                                    for(ExtraItem e : purchaseItem.ExtraItems) {
                                        if(e.name.equals(mItem.ExtraItems.get(i).name))
                                            checkBox.setChecked(true);
                                    }
                                }
                                checkBox.setId(-5 + i);
                                checkBox.setTextSize(20);
                                checkBox.setOnClickListener(extraListener);
                                extraItemsNames[i] = checkBox;
                                layout.addView(checkBox);
                            }
                            CheckBoxesAdapter adapter = new CheckBoxesAdapter(extraItemsNames);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    boolean isChecked = ((CheckBox)view).isChecked();
                                    if(isChecked)
                                        purchaseItem.ExtraItems.add(mItem.AddableItems.get(i));
                                    else
                                        purchaseItem.ExtraItems.remove(mItem.ExtraItems.get(i));
                                    Toast.makeText(ItemActivity.this, mItem.ExtraItems.get(i).name + "تم اضافتها ", Toast.LENGTH_SHORT).show();
                                }
                            });

                            build.setView(layout);
                            build.setNeutralButton("تم", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            build.create().show();
                        }
                    });
                }
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder build = new AlertDialog.Builder(ItemActivity.this);
                        ListView listView = new ListView(ItemActivity.this);
                        LinearLayout layout = new LinearLayout(ItemActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(10,10,10,10);
                        addingItemsNames = new CheckBox[mItem.AddableItems.size()];
                        for(int i = 0; i < mItem.AddableItems.size(); i++) {
                            CheckBox checkBox = new CheckBox(ItemActivity.this);
                            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            checkBox.setText(mItem.AddableItems.get(i).name);
                            if(purchaseItem.AddableItems.size() > 0) {
                                for(ExtraItem e : purchaseItem.AddableItems) {
                                    if(e.name.equals(mItem.AddableItems.get(i).name))
                                        checkBox.setChecked(true);
                                }
                            }
                            checkBox.setId(-5 + i);
                            checkBox.setTextSize(20);
                            checkBox.setOnClickListener(addListener);
                            addingItemsNames[i] = checkBox;
                            layout.addView(checkBox);
                        }
                        CheckBoxesAdapter adapter = new CheckBoxesAdapter(addingItemsNames);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                boolean isChecked = ((CheckBox)view).isChecked();
                                if(isChecked)
                                    purchaseItem.AddableItems.add(mItem.AddableItems.get(i));
                                else
                                    purchaseItem.AddableItems.remove(mItem.AddableItems.get(i));
                                Toast.makeText(ItemActivity.this, mItem.AddableItems.get(i).name + "تم اضافتها ", Toast.LENGTH_SHORT).show();
                            }
                        });

                        build.setView(layout);
                        build.setNeutralButton("تم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        build.create().show();
                    }
                });
                withoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder build = new AlertDialog.Builder(ItemActivity.this);
                        LinearLayout layout = new LinearLayout(ItemActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(10,10,10,10);
                        withoutItemsNames = new CheckBox[mItem.WithoutItems.size()];
                        for(int i = 0; i < mItem.WithoutItems.size(); i++) {
                            CheckBox checkBox = new CheckBox(ItemActivity.this);
                            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            checkBox.setText(mItem.WithoutItems.get(i).name);
                            if(purchaseItem.WithoutItems.size() > 0) {
                                for(ExtraItem e : purchaseItem.WithoutItems) {
                                    if(e.name.equals(mItem.WithoutItems.get(i).name))
                                        checkBox.setChecked(true);
                                }
                            }
                            checkBox.setId(-5 + i);
                            checkBox.setTextSize(20);
                            checkBox.setOnClickListener(withoutListener);
                            withoutItemsNames[i] = checkBox;
                            layout.addView(checkBox);
                        }

                        build.setView(layout);
                        build.setNeutralButton("تم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        build.create().show();
                    }
                });
                TextView i = findViewById(R.id.itemName);
                i.setText(name);
                setTitle(name);
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cart = pref.getString("cart", null);
                        purchaseItem.qty = Double.parseDouble(qty.getText().toString());
                        Log.d("Double is " , Double.parseDouble(qty.getText().toString()) + "");
                        Log.d("Stored is ", purchaseItem.qty + "");
                        if(cart != null) {
                            ActiveCart activeCart = new ActiveCart();
                            activeCart.deserialize(cart);
                            activeCart.addItem(purchaseItem);
                            editor.remove("cart");
                            editor.putString("cart", activeCart.toObject().toString());
                        } else {
                            ActiveCart activeCart = new ActiveCart();
                            activeCart.addItem(purchaseItem);
                            editor.putString("cart", activeCart.toObject().toString());
                        }
                        editor.apply();
                       // Toast.makeText(ItemActivity.this, "تم أضافه "+ purchaseItem.itemName, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    View.OnClickListener extraListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean isChecked = ((CheckBox)view).isChecked();
            int id = view.getId();
            for(int i = 0; i < extraItemsNames.length; i++) {
                if(extraItemsNames[i].getId() == id) {
                    if(isChecked)
                        purchaseItem.AddExtraItem(mItem.ExtraItems.get(i));
                    else
                        purchaseItem.RemoveExtraItem(i);
                  //  Toast.makeText(ItemActivity.this, "Added/Removed Item", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean isChecked = ((CheckBox)view).isChecked();
            int id = view.getId();
            for(int i = 0; i < addingItemsNames.length; i++) {
                if(addingItemsNames[i].getId() == id) {
                    if(isChecked) {
                        if(mItem.maxAddableItems == -1 || addedChild < mItem.maxAddableItems) {
                            purchaseItem.AddAddableItem(mItem.AddableItems.get(i));
                            addedChild++;
                        } else {
                            Toast.makeText(ItemActivity.this,
                                    "لا يمكنك اختيار اكثر من " + mItem.maxAddableItems + " من الاختيارات " ,
                                    Toast.LENGTH_SHORT).show();
                            ((CheckBox)view).setChecked(false);
                        }
                    }
                    else {
                        purchaseItem.RemoveAddableItem(i);
                        addedChild--;
                    }
                   // Toast.makeText(ItemActivity.this, "Added/Removed Item", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    View.OnClickListener withoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean isChecked = ((CheckBox)view).isChecked();
            int id = view.getId();
            for(int i = 0; i < withoutItemsNames.length; i++) {
                if(withoutItemsNames[i].getId() == id) {
                    if(isChecked) {
                        purchaseItem.AddWithoutItem(mItem.WithoutItems.get(i));
                    }
                    else {
                        purchaseItem.RemoveWithoutItem(i);
                    }
                    // Toast.makeText(ItemActivity.this, "Added/Removed Item", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
