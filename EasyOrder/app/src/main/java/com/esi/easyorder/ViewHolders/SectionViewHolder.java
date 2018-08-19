package com.esi.easyorder.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.esi.easyorder.R;

import pl.polidea.webimageview.WebImageView;

/**
 * Created by Server on 25/03/2018.
 */

public class SectionViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;
    public WebImageView image;
    public View itemView;
    public int sectionID;
    public int categoryID;
    public SectionViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.sectionName);
        image = itemView.findViewById(R.id.sectionImage);
        itemView.findViewById(R.id.sectionPrice).setVisibility(View.GONE);
        this.itemView = itemView;
    }
}
