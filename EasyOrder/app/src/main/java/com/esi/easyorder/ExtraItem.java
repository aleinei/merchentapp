package com.esi.easyorder;

import org.json.JSONException;
import org.json.JSONObject;


public class ExtraItem {

    public int ID;
    public String name;
    public boolean AddToPrice;
    public double price;
    public double Qty;
    public JSONObject toObject() {
        JSONObject item = new JSONObject();
        try {
            item.put("id", ID);
            item.put("name", name);
            item.put("addtoprice", AddToPrice);
            item.put("price", price);
            item.put("qty", Qty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }
}
