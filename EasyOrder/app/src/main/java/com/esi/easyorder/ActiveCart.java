package com.esi.easyorder;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Server on 28/02/2018.
 */

public class ActiveCart {
    public ArrayList<Item> Items;
    public double cost = 0;

    public ActiveCart() {
        Items = new ArrayList<>();
    }

    public JSONObject toObject() {
        JSONObject activeCart = new JSONObject();
        try {
            activeCart.put("cost", cost);
            JSONArray items = new JSONArray();
            for(Item i : Items) {
                items.put(i.toObject());
            }
            activeCart.put("items", items);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return activeCart;
    }

    public void deserialize(String object){
        try {
            JSONObject activeCart = new JSONObject(object);
            JSONArray items = activeCart.getJSONArray("items");
            this.cost = activeCart.getDouble("cost");
            Items.clear();
            for(int i = 0; i < items.length(); i++) {
                JSONObject it = items.getJSONObject(i);
                String name = it.getString("mItemName");
                double price = it.getDouble("itemPrice");
                int Id = it.getInt("itemId");
                double qty = it.getDouble("qty");
                boolean hasExtra = it.getBoolean("hasextra");
                int maxChild = it.getInt("maxChild");
                String souce = it.getString("Source");
//                String url = it.getString("itemURL");
                Item item = new Item(name, price, Id, "");
                item.qty = qty;
                item.maxAddableItems = maxChild;
                item.Source = souce;
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
                boolean hasAdd = it.getBoolean("hasadd");
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
                Items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addItem(Item item) {
        cost += (item.itemPrice * item.qty);
        for(Item item1: Items) {
            if(item1.itemName.equals(item.itemName)) {
                if(item1.ExtraItems.size() > 0) {
                    if(item.ExtraItems.size() == item1.ExtraItems.size()) {
                        boolean canAdd = true;
                        for(ExtraItem e : item1.ExtraItems) {
                            for(ExtraItem e2 : item.ExtraItems) {
                                if(e.name.equals(e2.name)) {
                                    canAdd = true;
                                    break;
                                }
                                canAdd = false;
                            }
                        }
                        if(canAdd) {
                            if(item.AddableItems.size() == item1.AddableItems.size()) {
                                if(item.AddableItems.size() == 0) {
                                    if(item.WithoutItems.size() == item1.WithoutItems.size()) {
                                        if(item.WithoutItems.size() == 0) {
                                            item1.qty += item.qty;
                                            return;
                                        } else {
                                            boolean canAdd3 = true;
                                            for(ExtraItem e : item1.WithoutItems) {
                                                for(ExtraItem e2 : item.WithoutItems) {
                                                    if(e.name.equals(e2.name)) {
                                                        canAdd3 = true;
                                                        break;
                                                    }
                                                    canAdd3 = false;
                                                }
                                            }
                                            if(canAdd3) {
                                                item1.qty += item.qty;
                                                return;
                                            }
                                        }
                                    }
                                }
                                boolean canAdd2 = true;
                                for(ExtraItem e : item1.AddableItems) {
                                    for(ExtraItem e2 : item.AddableItems) {
                                        if(e.name.equals(e2.name)) {
                                            canAdd2 = true;
                                            break;
                                        }
                                        canAdd2 = false;
                                    }
                                }
                                if(canAdd2) {
                                    if(item.WithoutItems.size() == item1.WithoutItems.size()) {
                                        boolean canAdd3 = true;
                                        for(ExtraItem e : item1.WithoutItems) {
                                            for(ExtraItem e2 : item.WithoutItems) {
                                                if(e.name.equals(e2.name)) {
                                                    canAdd3 = true;
                                                    break;
                                                }
                                                canAdd3 = false;
                                            }
                                        }
                                        if(canAdd3) {
                                            item1.qty += item.qty;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if(item1.AddableItems.size() > 0) {
                    if(item.AddableItems.size() == item1.AddableItems.size() && item.ExtraItems.size() == 0) {
                        boolean canAdd2 = true;
                        for(ExtraItem e : item1.AddableItems) {
                            for(ExtraItem e2 : item.AddableItems) {
                                if(e.name.equals(e2.name)) {
                                    canAdd2 = true;
                                    break;
                                }
                                canAdd2 = false;
                            }
                        }
                        if(canAdd2) {
                            if(item.WithoutItems.size() == item1.WithoutItems.size()) {
                                if(item.WithoutItems.size() == 0) {
                                    item1.qty += item.qty;
                                    return;
                                }
                                boolean canAdd3 = true;
                                for(ExtraItem e : item1.WithoutItems) {
                                    for(ExtraItem e2 : item.WithoutItems) {
                                        if(e.name.equals(e2.name)) {
                                            canAdd3 = true;
                                            break;
                                        }
                                        canAdd3 = false;
                                    }
                                }
                                if(canAdd3) {
                                    item1.qty += item.qty;
                                    return;
                                }
                            }
                        }
                    }
                } else if(item1.WithoutItems.size() > 0) {
                    if(item1.WithoutItems.size() == item.WithoutItems.size() && item.ExtraItems.size() == 0 && item.AddableItems.size() == 0) {
                        boolean canAdd3 = true;
                        for(ExtraItem e : item1.WithoutItems) {
                            for(ExtraItem e2 : item.WithoutItems) {
                                if(e.name.equals(e2.name)) {
                                    canAdd3 = true;
                                    break;
                                }
                                canAdd3 = false;
                            }
                        }
                        if(canAdd3) {
                            item1.qty += item.qty;
                            return;
                        }
                    }
                } else {
                    if(item.ExtraItems.size() == 0 && item.AddableItems.size() == 0 && item.WithoutItems.size() == 0) {
                        item1.qty += item.qty;
                        return;
                    }
                }
            }
        }
        Items.add(item);
    }

    public void DeleteItem(int i) {
        Item item = Items.get(i);
        cost -= item.itemPrice * item.qty;
        Items.remove(i);
        Log.d("ActiveCart", "Removed " + i);
    }

}
