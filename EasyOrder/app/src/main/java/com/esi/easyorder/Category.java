package com.esi.easyorder;

/**
 * Created by Server on 25/02/2018.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable{
    static final long serialVersionUID = 41L;
    public String name;
    public String Section_name;
    public int Id;
    public ArrayList<Item> items;
    public String category_url;

    public Category() {
        items = new ArrayList<>();
    }
    public Category(String name, int id, String Category_name, String url) {
        this.name = name;
        this.Id = id;
        this.Section_name = Category_name;
        items = new ArrayList<>();
        this.category_url = url;
    }

    @Override
    public String toString() {
        JSONObject category = new JSONObject();
        try {
            category.put("section_name", Section_name);
            category.put("name", name);
            category.put("id", Id);
            JSONArray itemss = new JSONArray();
            for(int i = 0; i < items.size(); i++) {
                itemss.put(items.get(i).toObject());
            }
            category.put("items", itemss);
            category.put("category_url", category_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  category.toString();
    }

    public void deserialize(String object) {
        try {
            JSONObject category = new JSONObject(object);
            this.name = category.getString("name");
            this.Section_name = category.getString("section_name");
            this.Id = category.getInt("id");
            JSONArray itemss = category.getJSONArray("items");
            items.clear();
            for(int i = 0; i < itemss.length(); i++) {
                JSONObject it = itemss.getJSONObject(i);
                String name = it.getString("itemName");
                double price = it.getDouble("itemPrice");
                int Id = it.getInt("itemId");
                boolean hasExtra = it.getBoolean("hasextra");
                boolean hasAdd = it.getBoolean("hasadd");
                int maxChild = it.getInt("maxChild");
                String source = it.getString("Source");
//                String url = it.getString("itemURL");
                Item item = new Item(name, price, Id, it.getString("imageURL"));
                item.maxAddableItems = maxChild;
                item.Source = source;
                item.unit = it.getInt("unit");
                if(hasExtra) {
                    JSONArray extraItems = it.getJSONArray("extraitems");
                    for(int x = 0; x < extraItems.length(); x++) {
                        JSONObject extraItem = extraItems.getJSONObject(x);
                        ExtraItem e = new ExtraItem();
                        e.name = extraItem.getString("name");
                        e.ID = extraItem.getInt("id");
                        e.AddToPrice = extraItem.getBoolean("addtoprice");
                        e.price = extraItem.getDouble("price");
                        e.Qty = extraItem.getDouble("qty");
                        item.ExtraItems.add(e);
                    }
                }
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
                        item.AddableItems.add(e);
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
                        item.WithoutItems.add(e);
                    }
                }
                items.add(item);
            }
           this.category_url = category.getString("category_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
