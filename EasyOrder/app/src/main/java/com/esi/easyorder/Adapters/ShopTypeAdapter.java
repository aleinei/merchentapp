package com.esi.easyorder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.esi.easyorder.R;
import com.esi.easyorder.ShopsSection;
import com.esi.easyorder.activites.ShopsActivity;

import java.util.ArrayList;

import pl.polidea.webimageview.WebImageView;

public class ShopTypeAdapter extends RecyclerView.Adapter<ShopTypeAdapter.ViewHolder>{

    Context mContext;
    ArrayList<ShopsSection> shops;
    public ShopTypeAdapter(Context mContext, ArrayList<ShopsSection> shops) {
        this.mContext = mContext;
        this.shops = shops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shoptype_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bind(shops.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopsActivity.class);
                intent.putExtra("shop",shops.get(position).toString());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView shoptypeName;
        WebImageView shoptypeImage;
        ImageButton info;
        public ViewHolder(View itemView) {
            super(itemView);
            shoptypeName = itemView.findViewById(R.id.shoptypename);
            shoptypeImage = itemView.findViewById(R.id.shoptypeimage);
            info = itemView.findViewById(R.id.shop_info);
            info.setVisibility(View.GONE);
        }

        void bind(ShopsSection shop) {
            shoptypeName.setText(shop.getName());
            shoptypeImage.setImageResource(shop.getImage());
        }
    }
}
