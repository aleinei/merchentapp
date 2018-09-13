package com.esi.easyorder.activites;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.Adapters.CheckBoxesAdapter;
import com.esi.easyorder.ExtraItem;
import com.esi.easyorder.Item;
import com.esi.easyorder.R;
import com.github.clans.fab.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.polidea.webimageview.WebImageView;

public class ItemActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    CoordinatorLayout itemLayout;
    FloatingActionButton cartConfirm;
    TextView itemName;
    LinearLayout gramLayout;
    TextView sectionName;
    TextView sectionPrice;
    WebImageView image;
    ImageButton increaseBtn;
    ImageButton decreaseBtn;
    TextView itemCount;
    ImageButton gIncreaseBtn;
    ImageButton gDecreaseBtn;
    TextView gItemCount;
    double kgCount = 0;
    int kgIncreaseAmount = 1;
    double gCount = 0;
    int gIncreaseAmount = 25;
    android.os.Handler increaseHandler = new Handler();
    Runnable kgIncreaseRunnable;
    Runnable gIncreaseRunnable;
    android.os.Handler decreaseHander = new Handler();
    Runnable kgDecreaseRunnable;
    Runnable gDecreaseRunnable;
    boolean stopCount = false;
    boolean isUsingKG;
    Item mItem;
    Item purchaseItem;
    CheckBox[] addingItemsNames;
    CheckBox[] extraItemsNames;
    CheckBox[] withoutItemsNames;
    Button extraButton;
    Button addButton;
    Button withoutButton;
    int addedChild = 0;
    double quantity;
    boolean isSwiping = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        itemLayout = findViewById(R.id.itemLayout);
        gramLayout = findViewById(R.id.gramLayout);
        increaseBtn = findViewById(R.id.increase);
        decreaseBtn = findViewById(R.id.decrease);
        gIncreaseBtn = findViewById(R.id.gIncrease);
        gDecreaseBtn = findViewById(R.id.gDecrease);
        itemCount = findViewById(R.id.itemCount);
        gItemCount = findViewById(R.id.gItemCount);
        sectionName = findViewById(R.id.sectionName);
        sectionPrice = findViewById(R.id.sectionPrice);
        image = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        cartConfirm = findViewById(R.id.addToOrder);
        pref = getSharedPreferences("global",0);
        editor = pref.edit();
        sectionPrice = findViewById(R.id.itemPrice);
        extraButton = findViewById(R.id.btnExtraItems);
        addButton = findViewById(R.id.btnAddItem);
        withoutButton = findViewById(R.id.btnWithouItem);


        String item = getIntent().getExtras().getString("item");
        if(item != null) {
            try {
                JSONObject it = new JSONObject(item);
                String itemName = it.getString("itemName");
                double itemPrice = it.getDouble("itemPrice");
                String name = it.getString("itemName");
                double price = it.getDouble("itemPrice");
                String itemImage = it.getString("imageURL");

                int Id = it.getInt("itemId");
                int maxChild = it.getInt("maxChild");
                String source = it.getString("Source");
                mItem =  new Item(itemName, itemPrice, Id, null);
                mItem.maxAddableItems = maxChild;
                mItem.unit = it.getInt("unit");
                purchaseItem = new Item(itemName, itemPrice, Id, null);
                purchaseItem.ExtraItems.clear();
                purchaseItem.Source = source;
                purchaseItem.unit = it.getInt("unit");
                boolean hasextra = it.getBoolean("hasextra");
                if(hasextra) {
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
                }
                this.itemName.setText(name);
                image.setImageURL(itemImage);
                if(mItem.unit <1000){
                    isUsingKG = false;
                    gramLayout.setVisibility(View.GONE);
                    this.sectionPrice.setText(String.valueOf(itemPrice)+ " "+getApplicationContext().getString(R.string.unitString));
                }else{
                    isUsingKG = true;
                    gramLayout.setVisibility(View.VISIBLE);
                    this.sectionPrice.setText(String.valueOf(itemPrice)+ " "+getApplicationContext().getString(R.string.unitCurrency));
                }
                updateCount();
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
            // Previous buttons start here
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

                cartConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cart = pref.getString("cart", null);
                        purchaseItem.qty = quantity;
                        if(quantity > 0){
                            Log.d("Double is " , quantity+ "");
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
                        }
                        editor.apply();
                        onBackPressed();

                        // Toast.makeText(ItemActivity.this, "تم أضافه "+ purchaseItem.itemName, Toast.LENGTH_SHORT).show();

                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        increaseDecrease();

    }

    ///previous click listeners start here
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

///////Runnables start here
    @SuppressLint("ClickableViewAccessibility")
    void increaseDecrease () {
        kgIncreaseRunnable = new Runnable() {
            @Override
            public void run() {
                if(!stopCount) {
                    kgCount +=kgIncreaseAmount;
                    updateCount();
                    increaseHandler.postDelayed(this, 500);
                }
            }
        };
        kgDecreaseRunnable = new Runnable() {
            @Override
            public void run() {
                if(!stopCount){
                    if(kgCount ==0)
                    {
                        return;
                    }
                    kgCount -=kgIncreaseAmount;
                    updateCount();
                    decreaseHander.postDelayed(this, 500);
                }

            }
        };
        gIncreaseRunnable = new Runnable() {
            @Override
            public void run() {
                if(!stopCount){
                    gCount +=gIncreaseAmount;
                    if(gCount >= 1000){
                        kgCount +=1;
                        gCount = gCount -1000;
                    }
                    updateCount();
                    decreaseHander.postDelayed(this,500);
                }

            }
        };
        gDecreaseRunnable = new Runnable() {
            @Override
            public void run() {

                if (!stopCount){
                    if (gCount ==0)
                    {
                        return;
                    }
                    gCount -= gIncreaseAmount;
                    updateCount();
                    decreaseHander.postDelayed(this, 500);
                }
            }
        };
        increaseBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isSwiping) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            increaseHandler.post(kgIncreaseRunnable);
                            stopCount = false;
                            return true;
                        case MotionEvent.ACTION_UP:
                            increaseHandler.removeCallbacks(kgIncreaseRunnable);
                            stopCount = true;
                            return true;
                    }
                }
                return false;
            }
        });


        decreaseBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        decreaseHander.post(kgDecreaseRunnable);
                        stopCount = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        decreaseHander.removeCallbacks(kgDecreaseRunnable);
                        stopCount = true;
                        return true;
                }
                return false;
            }
        });
        gIncreaseBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        increaseHandler.post(gIncreaseRunnable);
                        stopCount = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        increaseHandler.removeCallbacks(gIncreaseRunnable);
                        stopCount = true;
                        return true;
                }
                return false;
            }
        });
        gDecreaseBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        decreaseHander.post(gDecreaseRunnable);
                        stopCount = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        decreaseHander.removeCallbacks(gDecreaseRunnable);
                        stopCount = true;
                        return true;
                }
                return false;
            }
        });

    }



    void updateCount(){

        if(isUsingKG) {
            itemCount.setText(kgCount + " " + getString(R.string.kg));
            gItemCount.setText(gCount + " " + getString(R.string.grams));
        } else{
            if(kgCount >1){
                itemCount.setText((int)kgCount + " " + getString(R.string.units));
            }else{
                itemCount.setText((int)kgCount + " " + getString(R.string.unit));
            }
        }
        double num = kgCount + (gCount / 1000);
        quantity = Math.round(num *1000)/1000.000;
    }

}