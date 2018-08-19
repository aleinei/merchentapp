package com.esi.easyorder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esi.easyorder.MenuData;
import com.esi.easyorder.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MenuAdapter extends BaseAdapter {

    MenuData data;
    LayoutInflater inflater;
    Context context;
    int start;
    int count;
    public MenuAdapter(Context context, MenuData data, int start, int count) {

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.start = start;
        this.count = count;
    }

    @Override
    public int getCount() {
        return data.Sections.size();
    }

    @Override
    public Object getItem(int i) {
        return data.Sections.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.Sections.get(i).Id;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View view2 = inflater.inflate(R.layout.section_field_layout, null);
        TextView sectionName = view2.findViewById(R.id.sectionName);
        ImageView image = view2.findViewById(R.id.sectionImage);
        ImageLoader loader = ImageLoader.getInstance();
        loader.displayImage(data.Sections.get(i).Image_URL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.d("Load failed", data.Sections.get(i).Image_URL);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d("Load Completed", "Should work");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        sectionName.setText(data.Sections.get(i).name);
        return view2;
    }
}
