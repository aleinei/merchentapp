package com.esi.easyorder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.esi.easyorder.Section;
import com.esi.easyorder.ViewHolders.SectionViewHolder;
import com.esi.easyorder.activites.CategoryActivity;

/**
 * Created by Server on 25/03/2018.
 */

public class SectionReycleAdapter extends RecyclerView.Adapter<SectionViewHolder> {
    Context context;
    Section data;
    int SectionID;
    String UIType;
    public SectionReycleAdapter(Context context, Section data) {
        this.context = context;
        this.data = data;
        UIType = PreferenceManager.getDefaultSharedPreferences(context).getString("uiType", "textandpictures");
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_field_layout, null);
        SectionViewHolder holder = new SectionViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SectionViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final int pos = position;
        holder.textView.setText(data.categories.get(position).name);
        holder.sectionID = SectionID;
        holder.categoryID = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryActivity = new Intent(context, CategoryActivity.class);
                categoryActivity.putExtra("category", data.categories.get(position).toString());
                categoryActivity.putExtra("sectionId", SectionID);
                categoryActivity.putExtra("categoryId", pos);
                context.startActivity(categoryActivity);
            }
        });
        if(UIType.equals("textandpictures") || UIType.equals("pictures"))
            holder.image.setImageURL(data.categories.get(position).category_url);
        else if(UIType.equals("text"))
            holder.image.setVisibility(View.GONE);
        if(UIType.equals("pictures"))
            holder.textView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.categories.size();
    }
}

