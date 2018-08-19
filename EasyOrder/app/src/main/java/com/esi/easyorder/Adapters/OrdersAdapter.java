package com.esi.easyorder.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esi.easyorder.Order;
import com.esi.easyorder.R;

import java.util.ArrayList;


public class OrdersAdapter extends BaseAdapter {
    ArrayList<Order> orders;
    Context context;
    public OrdersAdapter(Context context, ArrayList<Order> orders)
    {
        this.context = context;
        this.orders = orders;
    }
    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return orders.get(i).ID;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = LayoutInflater.from(context).inflate(R.layout.order_item_layout, null);
        TextView orderNumber = view2.findViewById(R.id.orderNumber);
        TextView orderPrice = view2.findViewById(R.id.orderPrice);
        TextView orderStatus = view2.findViewById(R.id.orderStatus);
        orderNumber.setText("Order Number " + (i + 1));
        orders.get(i).ID = i + 1;
        orderPrice.setText(orders.get(i).cartOrder.cost + " L.E");
        if(orders.get(i).delivered)
        {
            orderStatus.setText("Delievered");
            orderStatus.setTextColor(Color.parseColor("#ee3b0e"));
        }
        else
        {
            orderStatus.setText("On Going");
            orderStatus.setTextColor(Color.parseColor("#72f231"));
        }
        return view2;
    }
}
