package com.esi.easyorder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Server on 12/04/2018.
 */

public class Order {

    public ActiveCart cartOrder;
    public boolean viewd;
    public int ID;
    public String OrderAddress;
    public boolean delivered = false;

    public JSONObject toObject()
    {
        JSONObject object = new JSONObject();
        try {
            object.put("cart", cartOrder.toObject().toString());
            object.put("viewd", viewd);
            object.put("id", ID);
            object.put("address", OrderAddress);
            object.put("delivered", delivered);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  object;
    }

    public void Deseralize(String object)
    {
        try {
            JSONObject order = new JSONObject(object);
            ActiveCart cart = new ActiveCart();
            cart.deserialize(order.getString("cart"));
            cartOrder = cart;
            viewd = order.getBoolean("viewd");
            ID = order.getInt("id");
            OrderAddress = order.getString("address");
            delivered = order.getBoolean("delivered");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
