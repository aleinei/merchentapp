package com.esi.easyorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShopsSection {

    public ArrayList<Shop> shops = new ArrayList<>();

    public String name = "" ;
    public int image;
    public String type = "";

    public ShopsSection() {

    }

    boolean imageExists=false;
    boolean isImageExists(){
        //check for image in db
        if(imageExists)
        {
            image = 0;
            return true;
        }

        return false;
    }
    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public boolean containShop(String dbName) {
        for(Shop shop : shops) {
            if(shop.dbName.equals(dbName)) return true;
        }
        return false;
    }
    @Override
    public String toString() {
        JSONObject shopSection = new JSONObject();
        try {
            shopSection.put("name", name);
            shopSection.put("image", image);
            shopSection.put("type", type);
            JSONArray shopsArray = new JSONArray();
            for(Shop shop : shops) {
                shopsArray.put(shop.toString());
            }
            shopSection.put("shops", shopsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shopSection.toString();
    }

    public void toClass(String object) {
        try {
            JSONObject shopSection = new JSONObject(object);
            this.name = shopSection.getString("name");
            this.image = shopSection.getInt("image");
            this.type = shopSection.getString("type");
            JSONArray shopsArray = shopSection.getJSONArray("shops");
            for(int i = 0; i < shopsArray.length(); i++) {
                Shop shop = new Shop();
                shop.toClass(shopsArray.getString(i));
                shops.add(shop);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
