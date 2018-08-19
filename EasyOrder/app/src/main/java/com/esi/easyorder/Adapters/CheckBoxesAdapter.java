package com.esi.easyorder.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

/**
 * Created by Server on 24/03/2018.
 */

public class CheckBoxesAdapter extends BaseAdapter {

    CheckBox[] boxes;
    public CheckBoxesAdapter(CheckBox[] boxes) {
        this.boxes = boxes;
    }
    @Override
    public int getCount() {
        return boxes.length;
    }

    @Override
    public Object getItem(int i) {
        return boxes[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        boxes[i].setPadding(10,10,10,10);
        boxes[i].setTextSize(20);
        return boxes[i];
    }
}
