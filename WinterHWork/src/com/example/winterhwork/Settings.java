package com.example.winterhwork;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Settings extends Activity{
	
	
	private RadioButton forwhite;
	private RadioButton forblack;
	private int team;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		forwhite = (RadioButton)findViewById(R.id.radioButton1);
		forblack = (RadioButton)findViewById(R.id.radioButton2);
				
		final EditText rowNumber = (EditText)findViewById(R.id.tt);
		final EditText colNumber= (EditText)findViewById(R.id.editText2);
		final EditText name = (EditText)findViewById(R.id.editText3);
		final EditText surname = (EditText)findViewById(R.id.editText4);
		final EditText warningMessage = (EditText)findViewById(R.id.editText5);
		final MediaPlayer click = MediaPlayer.create(Settings.this, R.raw.click);
		
		warningMessage.setEnabled(false);
		
		Button savebutton = (Button)findViewById(R.id.button1);
		
		forwhite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				team = 1; // for white knight
				forblack.setChecked(false);
			}
		});
		
		forblack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				team = 2; // for black knight
				forwhite.setChecked(false);
			}
		});
		
		savebutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub				
				
				if( name.getText().toString().trim().equals("") || surname.getText().toString().trim().equals("") 
				||  rowNumber.getText().toString().trim().equals("") 
				||  colNumber.getText().toString().trim().equals("")
				|| ( !forblack.isChecked() && !forwhite.isChecked() )  ){
					
					warningMessage.setText("You must fill all the blanks!!!");
					
				}else if( Integer.parseInt(rowNumber.getText().toString()) <4 ||
						Integer.parseInt(colNumber.getText().toString())   <4 ||
						Integer.parseInt(rowNumber.getText().toString()) >9 ||
						Integer.parseInt(colNumber.getText().toString()) >9 ){
						warningMessage.setText("Col and Row numbers should be 4-9!");
				}else {
					
					final String Name = name.getText().toString();
					final String SurName = surname.getText().toString();
					final int numberOfRow = Integer.parseInt(rowNumber.getText().toString());
					final int numberOfCol = Integer.parseInt(colNumber.getText().toString());
					
					Intent anIntent = new Intent(Settings.this , Game.class );
					anIntent.putExtra("name", Name);
					anIntent.putExtra("surname", SurName);
					anIntent.putExtra("rown", numberOfRow);
					anIntent.putExtra("coln", numberOfCol);
					anIntent.putExtra("wteam", team );
					warningMessage.setText("");
					click.start();
					startActivity(anIntent);
				}
			}
			
		});
	}
	
	
}
