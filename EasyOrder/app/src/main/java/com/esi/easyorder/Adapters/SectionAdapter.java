package com.esi.easyorder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esi.easyorder.R;
import com.esi.easyorder.Section;

/**
 * Created by Server on 25/02/2018.
 */

public class SectionAdapter extends BaseAdapter {
    Section data;
    LayoutInflater inflater;
    Context context;
    public SectionAdapter(Context context, Section data) {

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.categories.size();
    }

    @Override
    public Object getItem(int i) {
        return data.categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.categories.get(i).Id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = inflater.inflate(R.layout.section_field_layout, null);
        TextView sectionName = view2.findViewById(R.id.sectionName);
        sectionName.setText(data.categories.get(i).name);

        return view2;
    }
}
