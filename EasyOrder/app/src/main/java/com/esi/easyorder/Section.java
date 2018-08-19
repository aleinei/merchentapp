package com.esi.easyorder;

/**
 * Created by Server on 25/02/2018.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Section implements Serializable{
    static final long serialVersionUID = 40L;
    public String name;
    public int Id;
    public String Image_URL;
    public ArrayList<Category> categories;

    public Section() {
        categories = new ArrayList<>();
    }

    public Section(String name, int Id, String url) {
        this.name = name;
        this.Image_URL = url;
        this.Id = Id;
        categories = new ArrayList<>();
    }

    @Override
    public String toString() {
        JSONObject section = new JSONObject();
        try {
            section.put("name", name);
            section.put("id", Id);
            section.put("image", Image_URL);
            JSONArray categories = new JSONArray();
            for(int i = 0 ; i < this.categories.size(); i++) {
                categories.put(this.categories.get(i).toString());
            }
            section.put("categories", categories);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return section.toString();
    }

    public void deseralize(String object) {
        try {
            JSONObject section = new JSONObject(object);
            this.name = section.getString("name");
            this.Id = section.getInt("id");
            this.Image_URL = section.getString("image");
            JSONArray categories = section.getJSONArray("categories");
            this.categories.clear();
            for(int i = 0; i < categories.length(); i++) {
                String cat = categories.getString(i);
                Category category = new Category();
                category.deserialize(cat);
                this.categories.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
