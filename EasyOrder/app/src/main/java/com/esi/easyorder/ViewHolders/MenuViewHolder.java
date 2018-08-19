package com.esi.easyorder.ViewHolders;



import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.esi.easyorder.R;

import pl.polidea.webimageview.WebImageView;

public class MenuViewHolder extends RecyclerView.ViewHolder {
    public TextView sectionName;
    public WebImageView image;
    public View itemView;
    public MenuViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        sectionName = itemView.findViewById(R.id.sectionName);
        itemView.findViewById(R.id.sectionPrice).setVisibility(View.GONE);
        image = itemView.findViewById(R.id.sectionImage);
    }



}
