package com.esi.easyorder.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.esi.easyorder.Adapters.MenuReycleAdapter;
import com.esi.easyorder.MyContextWrapper;
import com.esi.easyorder.R;
import com.esi.easyorder.activites.MenuActivity;


public class MenuFragment extends Fragment {

    public MenuActivity menuActivity;
    RecyclerView recyclerView;
    LinearLayout gridViewLayout;
    LinearLayout loadingStart;
    boolean loaded = false;
    SharedPreferences pref;
    String language;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        language  = pref.getString("Language","en");
        View view = inflater.inflate(R.layout.menu_layout, null);
        loadingStart = view.findViewById(R.id.loadingStart);
        gridViewLayout = view.findViewById(R.id.menuGridLayout);
        if(menuActivity == null) {
            menuActivity = (MenuActivity)getActivity();
        }
        if(menuActivity != null && menuActivity.menuLoaded)
            LoadMenu();
        else
            Log.d("Something", (menuActivity != null) + " " + menuActivity.menuLoaded);
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
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        language = preferences.getString("Language", "en");

        attachBaseContext(MyContextWrapper.wrap(newBase, language));
    }
}
