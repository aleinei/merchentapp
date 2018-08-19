package com.esi.easyorder;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class MenuData implements Serializable{
    static final long serialVersionUID = 42L;
    public ArrayList<Section> Sections;


    public MenuData() {
        Sections = new ArrayList<>();
    }

    @Override
    public String toString() {
        JSONObject menuData = new JSONObject();
        JSONArray sections = new JSONArray();
        for(int i = 0; i < Sections.size(); i++) {
            sections.put(Sections.get(i).toString());
        }
        try {
            menuData.put("sections", sections);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return menuData.toString();
    }

    public void deserialize(String object) {
        try {
            JSONObject menuData = new JSONObject(object);
            JSONArray sections = menuData.getJSONArray("sections");
            Sections.clear();
            for(int i = 0 ; i < sections.length(); i++) {
                String se = sections.getString(i);
                Section section = new Section();
                section.deseralize(se);
                Sections.add(section);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Section getSectionWhereId(int sectionId) {
        for(Section s : this.Sections) {
            if(s.Id == sectionId) {
                return s;
            } else {
                Log.d("Section Id", s.Id + "");
            }
        }
        return null;
    }
}