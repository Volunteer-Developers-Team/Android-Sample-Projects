package com.shopping.assistant.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shopping.assistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment {

    private static final int REQUEST_CODE = 1;

    public InventoryFragment(){

    }


    public static InventoryFragment newInstance(int actiontype, int background) {
        Bundle args = new Bundle();
        InventoryFragment fragment = new InventoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.markets, container, false);


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE ) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

            }
            if(resultCode == Activity.RESULT_CANCELED){
                Log.d("Result Cancelled ", "");
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
