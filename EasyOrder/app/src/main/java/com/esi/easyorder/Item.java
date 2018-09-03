package com.esi.easyorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class Item implements Serializable{
    static final long serialVersionUID = 43L;
    public String itemName = "";
    public double itemPrice = 0;
    public String itemDesc = "";
    public int id = -1;
    public String imageURL = "";
    public double qty = 1;
    public ArrayList<ExtraItem> ExtraItems;
    public ArrayList<ExtraItem> AddableItems;
    public ArrayList<ExtraItem> WithoutItems;
    public int maxAddableItems = -1;
    public String Source = "";
    public int unit = 1;

    public String getQty(String quantity) {
        String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";

            char[] chars = new char[quantity.length()];
            for (int i = 0; i < quantity.length(); i++) {
                char ch = quantity.charAt(i);
                if (ch >= 0x0660 && ch <= 0x0669)
                    ch -= 0x0660 - '0';
                else if (ch >= 0x06f0 && ch <= 0x06F9)
                    ch -= 0x06f0 - '0';
                chars[i] = ch;
            }
            return new String(chars);
        }



    public Item(String name, double price, int id, String image) {
        itemName = name;
        itemPrice = price;
        this.id = id;
        imageURL = image;
        qty = 1;
        ExtraItems = new ArrayList<>();
        AddableItems = new ArrayList<>();
        WithoutItems = new ArrayList<>();
        Source = "";
    }

    public JSONObject toObject() {
        JSONObject item = new JSONObject();
        try {
            item.put("itemName", itemName);
            item.put("mItemName", GetModifiedName());
            item.put("itemPrice", itemPrice);
            item.put("itemDesc", itemDesc);
            item.put("itemId", id);
            item.put("imageURL", imageURL);
            item.put("qty",qty);
            item.put("maxChild", maxAddableItems);
            item.put("Source", Source);
            item.put("unit", unit);
            if(ExtraItems.size() > 0) {
                item.put("hasextra", true);
                JSONArray extraItems = new JSONArray();
                for(ExtraItem et : ExtraItems) {
                    extraItems.put(et.toObject());
                }
                item.put("extraitems", extraItems);
            } else {
                item.put("hasextra", false);
            }
            if(AddableItems.size() > 0) {
                item.put("hasadd", true);
                JSONArray addableItems = new JSONArray();
                for(ExtraItem et : AddableItems) {
                    addableItems.put(et.toObject());
                }
                item.put("addableitems", addableItems);
            } else {
                item.put("hasadd", false);
            }
            if(WithoutItems.size() > 0) {
                item.put("haswithout", true);
                JSONArray withoutItems = new JSONArray();
                for(ExtraItem et : WithoutItems) {
                    withoutItems.put(et.toObject());
                }
                item.put("withoutitems", withoutItems);
            } else {
                item.put("haswithout", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    public String GetModifiedName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemName);
        if(ExtraItems.size() > 0) {
            stringBuilder.append(" مع:");
            for(int x = 0; x < ExtraItems.size(); x++) {
                stringBuilder.append(ExtraItems.get(x).name);
                if(x < ExtraItems.size() - 1)
                    stringBuilder.append(", ");
            }
        }
        if(AddableItems.size() > 0) {
            stringBuilder.append(" باضافه: ");
            for(int x = 0; x < AddableItems.size(); x++) {
                stringBuilder.append(AddableItems.get(x).name);
                if(x < AddableItems.size() - 1)
                    stringBuilder.append(", ");
            }
        }
        if(WithoutItems.size() > 0) {
            stringBuilder.append(" بدون : ");
            for(int x = 0; x < WithoutItems.size(); x++) {
                stringBuilder.append(WithoutItems.get(x).name);
                if(x < WithoutItems.size() - 1)
                    stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public void AddAddableItem(ExtraItem item)
    {
        AddableItems.add(item);
        if(item.AddToPrice)
        {
            itemPrice += item.price * item.Qty;
        }
    }

    public void RemoveAddableItem(int pos)
    {
        if(AddableItems.get(pos).AddToPrice)
        {
            itemPrice -= AddableItems.get(pos).price * AddableItems.get(pos).Qty;
        }
        AddableItems.remove(pos);
    }

    public void AddExtraItem(ExtraItem item)
    {
        ExtraItems.add(item);
        if(item.AddToPrice)
        {
            itemPrice += item.price * item.Qty;
        }
    }

    public void RemoveExtraItem(int pos)
    {
        if(ExtraItems.get(pos).AddToPrice)
        {
            itemPrice -= ExtraItems.get(pos).price * ExtraItems.get(pos).Qty;
        }
        ExtraItems.remove(pos);
    }

    public void AddWithoutItem(ExtraItem item)
    {
        WithoutItems.add(item);
        if(item.AddToPrice)
        {
            itemPrice += item.price * item.Qty;
        }
    }

    public void RemoveWithoutItem(int pos)
    {
        if(WithoutItems.get(pos).AddToPrice)
        {
            itemPrice -= WithoutItems.get(pos).price * WithoutItems.get(pos).Qty;
        }
        WithoutItems.remove(pos);
    }
}
