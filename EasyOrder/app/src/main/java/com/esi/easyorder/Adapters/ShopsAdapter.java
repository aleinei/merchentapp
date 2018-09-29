package com.esi.easyorder.Adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esi.easyorder.R;
import com.esi.easyorder.Shop;
import com.esi.easyorder.User;
import com.esi.easyorder.activites.MenuActivity;
import com.esi.easyorder.activites.RegisterActivity;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import pl.polidea.webimageview.WebImageView;

public class ShopsAdapter extends Adapter {

    private ArrayList<Shop> shops;
    private Context mContext;
    User user;
    public ShopsAdapter(Context mContext, ArrayList<Shop> shops, User user) {
        this.mContext = mContext;
        this.shops = shops;
        this.user = user;
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
        FloatingActionButton infoButton;
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
            shopName.setVisibility(View.GONE);
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

                    Button shopLocation = infoView.findViewById(R.id.shop_direction);
                    shopLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(mContext);
                            LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                            boolean gpsStatus = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            if (!gpsStatus) {
                                Toast.makeText(mContext, "Please enable your gps to be able to locate your location", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(mContext, "Location permission is blocked, please allow the application to use GPS", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(final Location location) {
                                    if (location == null) {
                                        Log.d("Location", "Location is null");
                                        return;
                                    }
                                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + location.getLatitude() +"," + location.getLongitude()  +"&daddr=" + shop.getLat() + "," + shop.getLongtiude());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                        mContext.startActivity(mapIntent);
                                    }
                             }});
                        }
                    });

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
