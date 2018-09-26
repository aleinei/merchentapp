package com.esi.easyorder.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.esi.easyorder.R;
import com.esi.easyorder.Shop;
import com.esi.easyorder.activites.MenuActivity;

import java.util.ArrayList;

import pl.polidea.webimageview.WebImageView;

public class ShopsAdapter extends Adapter {

    private ArrayList<Shop> shops;
    private Context mContext;
    public ShopsAdapter(Context mContext, ArrayList<Shop> shops) {
        this.mContext = mContext;
        this.shops = shops;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shoptype_layout, parent, false);
        return new ShopsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((ShopsHolder)holder).bind(shops.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MenuActivity.class);
                intent.putExtra("dbName", shops.get(position).getDbName());
                intent.putExtra("loadHome", true);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }


    private class ShopsHolder extends RecyclerView.ViewHolder {

        TextView shopName;
        WebImageView shopImage;
        ImageButton infoButton;
        ImageView underConstruction;

        public ShopsHolder(View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shoptypename);
            shopImage = itemView.findViewById(R.id.shoptypeimage);
            infoButton = itemView.findViewById(R.id.shop_info);
            underConstruction = itemView.findViewById(R.id.underConstruction);
        }

        private void bind(final Shop shop) {
            final String lang = PreferenceManager.getDefaultSharedPreferences(mContext).getString("Language", "ar");
            shopName.setText(lang.equals("ar") ? shop.getName() : shop.getName_ar());
            shopImage.setImageResource(shop.getImage());
            shopImage.setImageURL("http://185.181.10.83/Pictures/Merchants/"+shop.getName().replace(" ","%20")+".jpg");
            if(!shop.getIsActive()){
            underConstruction.setVisibility(View.VISIBLE);
            }
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View infoView = LayoutInflater.from(mContext).inflate(R.layout.shop_info_layout, null, false);
                    TextView infoName = infoView.findViewById(R.id.shop_name);

                    infoName.setText(lang.equals("en") ? shop.getName() : shop.getName_ar());

                    TextView infoPhone = infoView.findViewById(R.id.shop_phone);
                    infoPhone.setText(mContext.getString(R.string.shop_phone, shop.getPhone()));

                    TextView infoAddress = infoView.findViewById(R.id.shop_address);
                    infoAddress.setText(mContext.getString(R.string.shop_address, shop.getAddress()));

                    AlertDialog dialog = new AlertDialog.Builder(mContext).setView(infoView).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    dialog.show();
                }
            });
        }
    }
}
