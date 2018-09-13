package com.esi.easyorder.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esi.easyorder.ActiveCart;
import com.esi.easyorder.R;
import com.esi.easyorder.activites.CartActivity;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

/**
 * Created by Server on 28/02/2018.
 */

public class CartAdapter extends BaseAdapter {
    public ActiveCart cart;
    LayoutInflater inflater;
    Context context;

    public CartAdapter(Context context, ActiveCart data) {

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cart = data;
    }

    @Override
    public int getCount() {
        return cart.Items.size();
    }

    @Override
    public Object getItem(int i) {
        return cart.Items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return cart.Items.get(i).id;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = inflater.inflate(R.layout.cart_item_layout, null);
        TextView quantityxprice = view2.findViewById(R.id.quantityxprice);
        TextView sectionName = view2.findViewById(R.id.cartItemName);
        DecimalFormat df = new DecimalFormat("#.000");
        String stringBuilder = (cart.Items.get(i).itemName);
        sectionName.setText(stringBuilder);
        TextView cartPrice = view2.findViewById(R.id.cartItemPrice);
        cartPrice.setText(df.format(cart.Items.get(i).itemPrice * cart.Items.get(i).qty));
        if(cart.Items.get(i).unit >=1000){
            quantityxprice.setText(cart.Items.get(i).qty +" "+context.getString(R.string.kg)+" × " + cart.Items.get(i).itemPrice + " "+context.getString(R.string.LE));
        }else {
            if(cart.Items.get(i).qty ==1) {
                quantityxprice.setText(cart.Items.get(i).qty + " " + context.getString(R.string.unit) + " × " + cart.Items.get(i).itemPrice + " " + context.getString(R.string.LE));
            }else{
                quantityxprice.setText(cart.Items.get(i).qty + " " + context.getString(R.string.units) + " × " + cart.Items.get(i).itemPrice + " " + context.getString(R.string.LE));
            }

        }
        return view2;
    }

    public void DeleteItem(int i ) {
        cart.DeleteItem(i);
        notifyDataSetChanged();
    }

    public ActiveCart GetCart()
    {
        return cart;
    }
}
