package com.esi.easyorder.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.esi.easyorder.Adapters.OrdersAdapter;
import com.esi.easyorder.R;
import com.esi.easyorder.User;
import com.esi.easyorder.activites.ViewOrderActivity;


public class OrdersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.orders_layout, container, false);
        GridView gridView = view.findViewById(R.id.ordersGridView);
        String user = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("user", "");
        if(!user.equals(""))
        {
            final User currentUser = new User();
            currentUser.Deseralize(user);
            OrdersAdapter adapter = new OrdersAdapter(getActivity(), currentUser.Orders);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), ViewOrderActivity.class);
                    intent.putExtra("order", currentUser.Orders.get(i).toObject().toString());
                    startActivity(intent);
                }
            });
        }
        return view;
    }
}
