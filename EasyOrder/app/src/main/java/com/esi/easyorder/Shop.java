package com.esi.easyorder;

import org.json.JSONException;
import org.json.JSONObject;

import pl.polidea.webimageview.WebImageView;

public class Shop {
    String shopName;
    String shopName_ar;
    int shopImage;
    String dbName;
    boolean isActive;
    String address;
    String phone;
    double longtiude;
    double latiude;

    public Shop() {}
    public Shop(String shopName, String shopName_ar, String dbName, int shopImage, boolean isActive, String address, String phone, double lat, double longt) {
        this.shopName = shopName;
        this.shopName_ar = shopName_ar;
        this.dbName = dbName;
        this.shopImage = shopImage;
        this.isActive = isActive;
        this.address = address;
        this.phone = phone;
        this.latiude = lat;
        this.longtiude = longt;
    }

    public String getName() {
        return shopName;
    }

    public int getImage() {
        return shopImage;
    }

    public String getDbName() {
        return dbName;
    }

    public String getName_ar() {return shopName_ar; }

    public boolean getIsActive() { return isActive; }

    public void setImage(int image) {
        this.shopImage = image;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        JSONObject shop = new JSONObject();
        try {
            shop.put("name", shopName);
            shop.put("shopImage", shopImage);
            shop.put("dbName", dbName);
            shop.put("name_ar", shopName_ar);
            shop.put("isActive", isActive);
            shop.put("address", address);
            shop.put("phone", phone);
            shop.put("longt", longtiude);
            shop.put("lat", latiude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shop.toString();
    }


    public void toClass(String object) {
        try {
            JSONObject shop = new JSONObject(object);
            this.shopName = shop.getString("name");
            this.shopImage = shop.getInt("shopImage");
            this.dbName = shop.getString("dbName");
            this.shopName_ar = shop.getString("name_ar");
            this.isActive = shop.getBoolean("isActive");
            this.phone = shop.getString("phone");
            this.address = shop.getString("address");
            this.latiude = shop.getDouble("lat");
            this.longtiude = shop.getDouble("longt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getLat() {
        return this.latiude;
    }

    public double getLongtiude() {
        return this.longtiude;
    }
}
