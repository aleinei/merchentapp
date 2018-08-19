package com.esi.easyorder.Adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;

import pl.polidea.webimageview.WebImageView;

/**
 * Created by Server on 28/02/2018.
 */

public class CategoryAdapter extends BaseAdapter {
    MenuData data;
    LayoutInflater inflater;
    Context context;
    int sectionId;
    int categoryId;
    boolean usePhoto;
    String UIType;
    public CategoryAdapter(Context context, MenuData data, int sId, int catId) {

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        sectionId = sId;
        categoryId = catId;
        usePhoto = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("usePhoto", true);
        UIType = PreferenceManager.getDefaultSharedPreferences(context).getString("uiType", "textandpictures");
    }

    @Override
    public int getCount() {
        return data.Sections.get(sectionId).categories.get(categoryId).items.size();
    }

    @Override
    public Object getItem(int i) {
        return  data.Sections.get(sectionId).categories.get(categoryId).items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return  data.Sections.get(sectionId).categories.get(categoryId).items.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = inflater.inflate(R.layout.section_field_layout, null);
        TextView sectionName = view2.findViewById(R.id.sectionName);
        TextView sectionPrice = view2.findViewById(R.id.sectionPrice);
        sectionName.setText( data.Sections.get(sectionId).categories.get(categoryId).items.get(i).itemName);
        WebImageView image = view2.findViewById(R.id.sectionImage);
        sectionPrice.setText(String.valueOf(data.Sections.get(sectionId).categories.get(categoryId).items.get(i).itemPrice) + " L.E");
        if(UIType.equals("textandpictures") || UIType.equals("pictures"))
            image.setImageURL(data.Sections.get(sectionId).categories.get(categoryId).items.get(i).imageURL);
        else if(UIType.equals("text"))
            image.setVisibility(View.GONE);

        if(UIType.equals("pictures")) {
            sectionName.setVisibility(View.GONE);
            sectionPrice.setVisibility(View.GONE);
        }
        return view2;
    }
}
