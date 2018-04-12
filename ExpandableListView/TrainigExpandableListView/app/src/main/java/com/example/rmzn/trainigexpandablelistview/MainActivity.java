package com.example.rmzn.trainigexpandablelistview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                prepareListData();
                listAdapter.notifyDataSetChanged();
            }
        }, 5000);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                List<String> comingSoon = new ArrayList<String>();
                comingSoon.add("2 Guns");
                comingSoon.add("The Smurfs 2");
                comingSoon.add("The Spectacular Now");
                comingSoon.add("The Canyons");
                comingSoon.add("Europa Report");
                listDataHeader.add("Coming Soon..");
                listDataChild.put(listDataHeader.get(2), comingSoon);
                listAdapter.notifyDataSetChanged();
            }
        }, 6000);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void showMessage(String s) {
        Toast toast = Toast.makeText( getApplicationContext() , s,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 25);
        toast.show();
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {


        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");


        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");


        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);

    }
}
