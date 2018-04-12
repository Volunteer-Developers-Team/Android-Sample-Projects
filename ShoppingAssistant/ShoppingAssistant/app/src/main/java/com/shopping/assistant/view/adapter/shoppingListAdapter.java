package com.shopping.assistant.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shopping.assistant.R;
import com.shopping.assistant.view.DriverFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by ustek on 24.04.2017.
 */

public class shoppingListAdapter extends BaseAdapter {

    Context context;
    ArrayList<DriverFragment.shoppingList> list;
    private static LayoutInflater inflater = null;

    public shoppingListAdapter(Context context, ArrayList<DriverFragment.shoppingList> lists ){
        this.context = context;
        this.list = lists;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    // There is no id!
    @Override
    public long getItemId(int i) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.shopping_list_item, null);

        TextView name = (TextView) vi.findViewById(R.id.listName);
        name.setText(list.get(position).getListName() );
        TextView data = (TextView) vi.findViewById(R.id.date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = formatter.format(list.get(position).getCreatedDate());
        data.setText( dateStr );

        return vi;
    }
}
