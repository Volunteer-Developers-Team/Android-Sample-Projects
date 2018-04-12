package com.example.winterhwork;

import java.util.Random;
import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class Game extends Activity{
	
	public int myPosition;
	private newAdapter anAdapter;
	private int pcPosition;
	private MediaPlayer winSound ;
	private MediaPlayer lostSound ;
	private AlertDialog.Builder alertDialog;;
	private TextView text1;
	private TextView text2;
	private int otherMovingCounter=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		text1 = (TextView)findViewById(R.id.text1);
		text2 = (TextView)findViewById(R.id.text2);
		winSound = MediaPlayer.create(this, R.raw.endinggood );
		lostSound = MediaPlayer.create(this, R.raw.endingbad );
		alertDialog  = new AlertDialog.Builder(this);
		
		//default values
		String playerName = "unnamed unsurnamed";
		final int theTeam;
		int rowNumber=5, colNumber=5;
		
		if( getIntent().getExtras() != null ){
			
			playerName = getIntent().getStringExtra("name")+" "+getIntent().getStringExtra("surname");
			rowNumber = getIntent().getIntExtra("rown", 5 );
			colNumber = getIntent().getIntExtra("coln", 5 );
			theTeam = getIntent().getIntExtra("wteam",0);			
			text1.setText(playerName);
			text2.setText("Computer");
			
		}else{
			//Default values if the user didn't enter Settings option
			text1.setText(playerName);
			text2.setText("Computer");
			rowNumber = 5;
			colNumber = 5;
			theTeam = 1;
		}
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		anAdapter = new newAdapter(this, rowNumber, colNumber, theTeam );
		anAdapter.setWidthSize(metrics.widthPixels/colNumber);
		final GridView chessBoard = (GridView)findViewById(R.id.gridView1);
		chessBoard.setNumColumns(colNumber);
		chessBoard.setColumnWidth(metrics.widthPixels/colNumber);
		chessBoard.setPadding(0, 10, 0, 10);
		chessBoard.setVisibility(View.VISIBLE);
		chessBoard.setStretchMode(GridView.NO_STRETCH);
		chessBoard.setAdapter(anAdapter);
		
		final int colN = colNumber;
		final int rowN = rowNumber;
		
		myPosition=0;
		pcPosition=rowN*colN-1;
		chessBoard.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	
            		Log.e("LOG2", "MyMoving");
	            	boolean status = userPlay(anAdapter.getPosX(position), anAdapter.getPosY(position));
		         	if( status && anAdapter.getItems()[position] != R.drawable.cross
		            	&& anAdapter.winner !=2                                         ){
		         		anAdapter.getItems()[position] = anAdapter.getItems()[myPosition];
		            	anAdapter.getItems()[myPosition] = R.drawable.cross;
		            	myPosition = position;
		            	
		            	chessBoard.setAdapter(anAdapter);
		      
		            	if( myPosition == pcPosition ){
		            		winSound.start();
		            		anAdapter.winner=1;
		            		chessBoard.setAdapter(anAdapter);
		            		finishGame("You won the game!");
		            		anAdapter.winner=0;
	            		}
		            	
		            	boolean status3 = findPlaceToMove(pcPosition, rowN, colN);
		            	if( !status3 ){
		            		Log.e("LOG3", "PcNotMoving");
		            		winSound.start();
		            		anAdapter.winner=1;
		            		finishGame("You won the game!");
		            		anAdapter.winner=0;
		            	}else if( status3 ){
		            		
		            	boolean stat = playPC(anAdapter.getPosX(myPosition),anAdapter.getPosY(myPosition) );
		            	if( stat && anAdapter.winner !=1 ){
		            		anAdapter.getItems()[myPosition] = anAdapter.getItems()[pcPosition];
		            		anAdapter.getItems()[pcPosition] = R.drawable.cross;
			            	pcPosition = myPosition;
			            	
			            	chessBoard.setAdapter(anAdapter);
			            	lostSound.start();
			            	anAdapter.winner =2;
			            	finishGame("You lost the game!");
			            	anAdapter.winner=0;
		            	}else{
		            	
			            	boolean status2 = false;
			            	otherMovingCounter=0;
			            	while( !status2 ){
			            	Log.e("LOG7", "RandomSelection");
			            	
			            	Random xR = new Random();
			            	Random yR = new Random();
			            	int ok = 0, posX=-1, posY=-1;
			            	
			            	while( ok == 0 ){
			            		Log.e("LOG7", "PcWhileLoop");
			            		posX = xR.nextInt(rowN);
			            		posY = yR.nextInt(colN);
			            		if( posX >= 0 && posY >= 0  )
			            			ok = 1;
			            	}
			            	
			            	status2 = playPC( posX, posY );
			            	if( status2 &&  anAdapter.winner !=1 && 
			            		anAdapter.getItems()[colN*posX +posY] != R.drawable.cross 
			            		&& colN*posX +posY != pcPosition                            ){
			            		
			            		++otherMovingCounter;
			            		if( !userPlay(posX, posY) || otherMovingCounter > 8 ){
				            		anAdapter.getItems()[colN*posX+posY] = anAdapter.getItems()[pcPosition];
				            		anAdapter.getItems()[pcPosition] = R.drawable.cross;
				            		pcPosition = colN*posX +posY;
					            	chessBoard.setAdapter(anAdapter);
			            		}else
			            			status2=false;
			            			
			            	}else
			            		status2=false;
			            		
			            }
			            	
		         	}
		         	}
		            	chessBoard.setAdapter(anAdapter);
		            	boolean status4 = findPlaceToMove(myPosition, rowN, colN);
		            	if( !status4 ){
		            		lostSound.start();
		            		anAdapter.winner=2;
		            		finishGame("You lost the game!");
		            		anAdapter.winner=0;
		            	}
	        	}
	        }
	        
	    });
	
	}
	
	
	private boolean findPlaceToMove( int pos, int row, int col ){
		for( int i=0; i<row*col ; ++i ){
			Log.e("LOG5", "findToMove");
			if( pos == myPosition ){
				boolean canPlace = userPlay( anAdapter.getPosX(i), anAdapter.getPosY(i) );
				if( canPlace && anAdapter.getItems()[i] != R.drawable.cross )
					return true;
			}else if( pos == pcPosition ){
				boolean canPlace = playPC( anAdapter.getPosX(i), anAdapter.getPosY(i) );
				if(canPlace && anAdapter.getItems()[i] != R.drawable.cross)
					return true;
			}
		}
		return false;
	}
	
	public void helpTheUser( int userPos, int rowN, int colN ){
		for( int i=0; i<rowN*colN; ++i ){
			if( userPlay(anAdapter.getPosX(i), anAdapter.getPosY(i)) ){
				
			}
		
		}
	}
	
	private boolean userPlay( int x, int y ){
		Log.e("LOG6", "UserPlay");
		if( ((anAdapter.getPosX(myPosition)-x == 2 && (anAdapter.getPosY(myPosition)-y == 1 ||
			anAdapter.getPosY(myPosition)-y == -1)) || ( anAdapter.getPosX(myPosition)-x == -2 && 
			(anAdapter.getPosY(myPosition)-y == 1 || anAdapter.getPosY(myPosition)-y == -1) ) ) ||
			((anAdapter.getPosX(myPosition)-x == 1 && (anAdapter.getPosY(myPosition)-y == 2 ||
			anAdapter.getPosY(myPosition)-y == -2)) || ( anAdapter.getPosX(myPosition)-x == -1 && 
			(anAdapter.getPosY(myPosition)-y == 2 || anAdapter.getPosY(myPosition)-y == -2) ) ))
			return true;
		else
			return false;
		
	}
	
	private boolean playPC( int x, int y ){
		if(		(anAdapter.getColNumber()*x+y) >= 0  &&	 (anAdapter.getColNumber()*x+y) <= 
				(anAdapter.getRowNumber()*anAdapter.getColNumber()-1)  &&
				( ((anAdapter.getPosX(pcPosition)-x == 2 && (anAdapter.getPosY(pcPosition)-y == 1 ||
				anAdapter.getPosY(pcPosition)-y == -1)) || ( anAdapter.getPosX(pcPosition)-x == -2 && 
				(anAdapter.getPosY(pcPosition)-y == 1 || anAdapter.getPosY(pcPosition)-y == -1) ) ) ||
				((anAdapter.getPosX(pcPosition)-x == 1 && (anAdapter.getPosY(pcPosition)-y == 2 ||
				anAdapter.getPosY(pcPosition)-y == -2)) || ( anAdapter.getPosX(pcPosition)-x == -1 && 
				(anAdapter.getPosY(pcPosition)-y == 2 || anAdapter.getPosY(pcPosition)-y == -2) ) )) )
				return true;
			else
				return false;
	}
	

	public int getPcPosition() {
		return pcPosition;
	}

	public void setPcPosition(int pcPosition) {
		this.pcPosition = pcPosition;
	}
	

	private void finishGame( String winner ){
		
		if( anAdapter.winner == 1 )
			alertDialog.setIcon(R.drawable.smiling);
		else
			alertDialog.setIcon(R.drawable.sad);
		
		alertDialog.setTitle("The End!");
			alertDialog.setMessage(winner)
			   .setCancelable(false)
			   .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog, int id) {
			    	   Intent intent = getIntent();
			    	   finish();startActivity(intent);
			       }
			   })
			   .setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog, int id) {
			    	   Intent backIntent = new Intent(Game.this, SplashScreen.class);
			    	   finish();startActivity(backIntent);
			       }
			   });
			
				
		AlertDialog alert = alertDialog.create();
		alert.show();
	}

	
}

