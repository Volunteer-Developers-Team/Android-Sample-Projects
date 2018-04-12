package com.shopping.assistant.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shopping.assistant.R;
import com.shopping.assistant.view.adapter.shoppingListAdapter;

import java.util.ArrayList;
import java.util.Date;


public class DriverFragment extends android.support.v4.app.Fragment{

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DriverFragment.
     */

    ArrayList<shoppingList> list;

    public static DriverFragment newInstance() {
        return new DriverFragment();
    }

    public DriverFragment() {
        list = new ArrayList<>();

        // Debugging Mode .. Dump Values :S
        shoppingList item = new shoppingList("Alis Veris Listem 1");
        shoppingList item2 = new shoppingList("Alis Veris 2");
        shoppingList item3 = new shoppingList("Alis 3");
        shoppingList item4 = new shoppingList("Alis Veris Listem 4-1_2");
        list.add(item);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        // TODO : server dan listeleri çek... ApiRequest
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);
        final ListView shoppingLists = (ListView) view.findViewById(R.id.shoppingLists);
        shoppingLists.setAdapter(new shoppingListAdapter( getActivity().getApplicationContext(), list));

        shoppingLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                shoppingList item = list.get(index);
                showMessage(item.listName + " " + item.getCreatedDate());

                //TODO : Start new actvivity for the list...
            }
        });

        return view;
    }

    public void setShoppingList( ArrayList<shoppingList> list ){
        this.list = list;
        notifyAll();
    }

    public class shoppingList{

        private String listName;
        private Date createdDate;

        public shoppingList( String name ){
            this.listName = name;
            createdDate = new Date();
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }

        public Date getCreatedDate() {
            return createdDate;
        }
    }

    private void showMessage(String s) {
        Toast toast = Toast.makeText( getActivity().getApplicationContext() , s,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 25);
        toast.show();
    }

}
