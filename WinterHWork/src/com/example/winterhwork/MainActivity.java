package com.example.winterhwork;

import android.os.Bundle;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen );
		
		EditText text1 = (EditText)findViewById(R.id.tt);
		text1.setEnabled(false);
		
		EditText text2 = (EditText)findViewById(R.id.editText2);
		text2.setEnabled(false);
		
		Thread splash = new Thread(){
		 	public void run(){
		 		try{
		 			sleep(3000);
		 			startActivity(new Intent( MainActivity.this, SplashScreen.class ));
		 			
		 		}
		 		catch( Exception e ){
		 			e.printStackTrace();
		 		}
		 		finally{
		 			finish();
		 		}	
		 	}
		};
		splash.start();
	}
}

