package com.esi.easyorder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esi.easyorder.Category;
import com.esi.easyorder.Item;
import com.esi.easyorder.MenuData;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.R;
import com.esi.easyorder.activites.CategoryActivity;
import com.esi.easyorder.activites.ItemActivity;
import com.esi.easyorder.activites.MainActivity;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.logging.Handler;

import pl.polidea.webimageview.WebImageView;

/**
 * Created by Server on 28/02/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemViewHolder> {
    Category data;
    LayoutInflater inflater;
    Context context;
    int sectionId;
    int categoryId;
    boolean usePhoto;
    String UIType;
    SharedPreferences pref;
    String language;

    public CategoryAdapter(Context context, Category data) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        language=pref.getString("Language","ar");
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        usePhoto = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("usePhoto", true);
        UIType = PreferenceManager.getDefaultSharedPreferences(context).getString("uiType", "textandpictures");
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder,  int position) {
        final int pos = position;
        holder.bind(data.items.get(position));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itemActivity = new Intent(context, ItemActivity.class);
                itemActivity.putExtra("item", data.items.get(pos).toObject().toString());
                context.startActivity(itemActivity);
            }
        });

    }

    @Override
    public long getItemId(int i) {
        return data.items.get(i).id;
    }

    @Override
    public int getItemCount() {
        return data.items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView sectionName;
        TextView sectionPrice;
        WebImageView image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            sectionName = itemView.findViewById(R.id.itemName);
            sectionPrice = itemView.findViewById(R.id.itemPrice);
            image = itemView.findViewById(R.id.itemImage);

        }

        @SuppressLint("ClickableViewAccessibility")
        void bind(final Item item) {
            if(item.unit >= 1000) {
                sectionPrice.setText(item.itemPrice+"");
                sectionPrice.setText(item.itemPrice+ " "+context.getString(R.string.unitCurrency) );
            }else{
                sectionPrice.setText(item.itemPrice+ " "+context.getString(R.string.unitString) );
            }

            sectionName.setText(item.itemName);


            if (UIType.equals("textandpictures") || UIType.equals("pictures"))
                image.setImageURL(item.imageURL);
            else if (UIType.equals("text"))
                image.setVisibility(View.GONE);

            if (UIType.equals("pictures")) {
                sectionName.setVisibility(View.GONE);
                sectionPrice.setVisibility(View.GONE);
            }


            }


    }

    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "en");

        attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }

}
