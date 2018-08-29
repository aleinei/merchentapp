package com.esi.easyorder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.esi.easyorder.Item;
import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.esi.easyorder.activites.CategoryActivity;
import com.esi.easyorder.activites.ItemActivity;

import java.text.DecimalFormat;
import java.util.logging.Handler;

import pl.polidea.webimageview.WebImageView;

/**
 * Created by Server on 28/02/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemViewHolder> {
    MenuData data;
    LayoutInflater inflater;
    Context context;
    int sectionId;
    int categoryId;
    boolean usePhoto;
    String UIType;
    public CategoryAdapter(Context context, MenuData data, int sId, int catId) {

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        sectionId = sId;
        categoryId = catId;
        usePhoto = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("usePhoto", true);
        UIType = PreferenceManager.getDefaultSharedPreferences(context).getString("uiType", "textandpictures");
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        holder.bind(data.Sections.get(sectionId).categories.get(categoryId).items.get(position));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemActivity = new Intent(context, ItemActivity.class);
                itemActivity.putExtra("item", data.Sections.get(sectionId).categories.get(categoryId).items.get(position).toObject().toString());
                context.startActivity(itemActivity);
            }
        });

    }

    @Override
    public long getItemId(int i) {
        return  data.Sections.get(sectionId).categories.get(categoryId).items.get(i).id;
    }

    @Override
    public int getItemCount() {
        return data.Sections.get(sectionId).categories.get(categoryId).items.size();
    }

   public class ItemViewHolder extends RecyclerView.ViewHolder {
       TextView sectionName;
       TextView sectionPrice;
       WebImageView image;
       CardView kgCard;
       ImageButton increaseBtn;
       ImageButton decreaseBtn;
       TextView itemCount;
       double count = 0.0;
       double increaseAmount = 0.005;
       android.os.Handler increaseHandler;
       Runnable increaseRunnable;
       android.os.Handler decreaseHander;
       Runnable decreaseRunnable;
       public ItemViewHolder(View itemView) {
           super(itemView);
            sectionName = itemView.findViewById(R.id.itemName);
            sectionPrice = itemView.findViewById(R.id.itemPrice);
            image = itemView.findViewById(R.id.itemImage);
            kgCard = itemView.findViewById(R.id.kgCard);
            increaseBtn = itemView.findViewById(R.id.increase);
            decreaseBtn = itemView.findViewById(R.id.decrease);
            itemCount = itemView.findViewById(R.id.itemCount);
            increaseHandler = new android.os.Handler(context.getMainLooper());
            decreaseHander = new android.os.Handler(context.getMainLooper());
       }

       @SuppressLint("ClickableViewAccessibility")
       void bind(final Item item) {
           increaseRunnable = new Runnable() {
               @Override
               public void run() {
                   count += increaseAmount;
                   Math.round(count);
                   updateCount();
                   item.qty = count;
                   ((CategoryActivity)context).addCartItem(item);
                   increaseHandler.postDelayed(this, 100);
               }
           };
           decreaseRunnable = new Runnable() {
               @Override
               public void run() {
                   if(count <= 0.000) return;
                   count -= increaseAmount;
                   if(count <= 0.000) count = 0.000;
                   Math.round(count);
                   updateCount();
                   item.qty = count;
                   ((CategoryActivity)context).removeCartItem(item);
                   decreaseHander.postDelayed(this, 100);
               }
           };
           increaseAmount = item.unit >= 1000 ? 0.005 : 1.0;
           sectionName.setText(item.itemName);
           sectionPrice.setText(item.itemPrice + " EGP");
           if(UIType.equals("textandpictures") || UIType.equals("pictures"))
               image.setImageURL(item.imageURL);
           else if(UIType.equals("text"))
               image.setVisibility(View.GONE);

           if(UIType.equals("pictures")) {
               sectionName.setVisibility(View.GONE);
               sectionPrice.setVisibility(View.GONE);
           }
           if(item.unit < 1000) kgCard.setVisibility(View.GONE);
           itemCount.setText("" + count);
           increaseBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   count += increaseAmount;
                   Math.round(count);
                   updateCount();
                   item.qty = count;
                   ((CategoryActivity)context).addCartItem(item);
               }
           });
           decreaseBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(count <= 0.00) return;
                   count -= increaseAmount;
                   if(count <= 0.000) count = 0.000;
                   Math.round(count);
                   updateCount();
                   item.qty = count;
                   ((CategoryActivity)context).removeCartItem(item);
               }
           });

           increaseBtn.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   switch(event.getAction()) {
                       case MotionEvent.ACTION_DOWN:
                           increaseHandler.post(increaseRunnable);
                           return true;
                       case MotionEvent.ACTION_UP:
                           increaseHandler.removeCallbacks(increaseRunnable);
                           return true;
                   }
                   return false;
               }
           });

           decreaseBtn.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   switch(event.getAction()) {
                       case MotionEvent.ACTION_DOWN:
                           decreaseHander.post(decreaseRunnable);
                           return true;
                       case MotionEvent.ACTION_UP:
                           decreaseHander.removeCallbacks(decreaseRunnable);
                           return true;
                   }
                   return false;
               }
           });
       }

       void updateCount() {
           DecimalFormat df = new DecimalFormat("0.000");
           itemCount.setText(df.format(count));
       }
   }
}
