package com.esi.easyorder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.esi.easyorder.Adapters.MenuReycleAdapter;
import com.esi.easyorder.R;
import com.esi.easyorder.activites.MenuActivity;


public class MenuFragment extends Fragment {

    public MenuActivity menuActivity;
    RecyclerView recyclerView;
    LinearLayout gridViewLayout;
    LinearLayout loadingStart;
    boolean loaded = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_layout, null);
        loadingStart = view.findViewById(R.id.loadingStart);
        gridViewLayout = view.findViewById(R.id.menuGridLayout);
        if(menuActivity.menuLoaded)
            LoadMenu();
        return view;
    }

    public void LoadMenu() {
        recyclerView = new RecyclerView(menuActivity);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2,1);
        GridLayoutManager lm2 = new GridLayoutManager(menuActivity, 8);
        lm2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(menuActivity.menuData.Sections.size() <= 4)
                    return 8;
                if(position % 5 == 0)
                    return 8;
                else
                    return 4;
            }
        });
        recyclerView.setLayoutManager(lm2);
        MenuReycleAdapter adapter = new MenuReycleAdapter(menuActivity, menuActivity.menuData);
        recyclerView.setAdapter(adapter);
        gridViewLayout.addView(recyclerView);
        loadingStart.setVisibility(View.GONE);
    }
}
