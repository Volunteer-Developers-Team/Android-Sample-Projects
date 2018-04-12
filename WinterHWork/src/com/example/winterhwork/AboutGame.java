package com.example.winterhwork;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class AboutGame extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about0);
		
		EditText text = (EditText)findViewById(R.id.tt);
		text.setEnabled(false);
		
	}

	
	
}
