package com.esi.easyorder;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Server on 12/04/2018.
 */

public class User {

    public int ID;
    public String username;
    public Location location;
    public String Telephone;
    public String Email;
    public String Address;
    public ArrayList<Order> Orders;
    public String Password;
    public User()
    {
        ID = -1;
        location = new Location("User Location");
        Orders = new ArrayList<>();
    }

    public JSONObject toObject()
    {
        JSONObject user = new JSONObject();
        try {
            user.put("ID", ID);
            user.put("username", username);
            user.put("email", Email);
            user.put("tele", Telephone);
            user.put("address", Address);
            user.put("long", location.getLongitude());
            user.put("lat", location.getLatitude());
            user.put("pass", Password);
            JSONArray orders = new JSONArray();
            for(Order order : Orders)
            {
                orders.put(order.toObject());
            }
            user.put("orders", orders);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void Deseralize(String object)
    {
        try {
            JSONObject user = new JSONObject(object);
            ID = user.getInt("ID");
            username = user.getString("username");
            location = new Location("User Location");
            location.setLatitude(user.getDouble("lat"));
            location.setLongitude(user.getDouble("long"));
            Email = user.getString("email");
            Telephone = user.getString("tele");
            Address = user.getString("address");
            Password = user.getString("pass");
            JSONArray orders = user.getJSONArray("orders");
            for(int i = 0; i < orders.length(); i++)
            {
                Order order = new Order();
                order.Deseralize(orders.getJSONObject(i).toString());
                Orders.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
