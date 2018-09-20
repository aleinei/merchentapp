package com.esi.easyorder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.esi.easyorder.ViewHolders.MenuViewHolder;
import com.esi.easyorder.activites.SectionActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Server on 25/03/2018.
 */

public class MenuReycleAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    private MenuData data;
    private Context context;
    String UIType;

    public MenuReycleAdapter(Context context, MenuData data) {
        this.data = data;
        this.context = context;
        UIType = PreferenceManager.getDefaultSharedPreferences(context).getString("uiType", "textandpictures");
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_field_layout, null);
        MenuViewHolder holder = new MenuViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, final int position) {
        final int pos = position;
        holder.sectionName.setText(data.Sections.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent category = new Intent(context, SectionActivity.class);
                category.putExtra("menuData", data.Sections.get(position).toString());
                category.putExtra("sectionId", pos);
                context.startActivity(category);
            }
        });
        if(UIType.equals("textandpictures") || UIType.equals("pictures"))
            holder.image.setImageURL(data.Sections.get(position).Image_URL);
        else if(UIType.equals("text"))
            holder.image.setVisibility(View.GONE);
        if(UIType.equals("pictures"))
            holder.sectionName.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.Sections.size();
    }
}
