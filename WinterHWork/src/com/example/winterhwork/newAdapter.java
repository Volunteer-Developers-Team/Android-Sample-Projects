package com.example.winterhwork;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class newAdapter extends BaseAdapter {
    private Context mContext;
    private int row,col;
    private int[] gameItems;
    private int sizeWidth;
    private int Team;
    protected int winner=0;
    
	
    public newAdapter(Context c, int row, int col, int team) {
        mContext = c;
        this.row = row;
        this.col = col;
        Team = team;
        createGameItems();
    }
	
    public int getPosY( int position ){
    	return (position-(position/col)*col);
    }
    
    public int getPosX( int position ){
    	return (position/col);
    }
    
    public int getBlackPosition(){
    	for( int i=0; i<row*col; ++i ){
    		if( gameItems[i] == R.drawable.blackknight )
    			return i;
    	}
    	return -1;
    }
    
    public int getWhitePosition(){
    	for( int i=0; i<row*col; ++i ){
    		if( gameItems[i] == R.drawable.whiteknight )
    			return i;
    	}
    	return -1;
    }
    
    public int[] getItems() {
        return gameItems;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(sizeWidth, sizeWidth));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(gameItems[position]);
        return imageView;
    }
    public void setWidthSize(int size){
    	sizeWidth = size;
    }
    
    private void createGameItems(){
    	
    	gameItems = new int[row*col];
    	int index;
    	for( int i=0; i<row ; ++i )
    		for( int j=0; j<col; ++j ){
    			index = i*col+j;
    		if( (row%2 == 0 && col%2 == 0 ) || col%2 == 0 ){	
    			if( (index+i)%2 == 0 )
    				gameItems[index] = R.drawable.blacksquare;
    			else
    				gameItems[index] = R.drawable.whitesquare;
    		}else{	
    			if( index%2 == 0 )
    				gameItems[index] = R.drawable.blacksquare;
    			else
    				gameItems[index] = R.drawable.whitesquare;
    		}
    		}
    	
    	if( Team == 1 ){
    		gameItems[0] = R.drawable.whiteknight;
    		gameItems[row*col-1] = R.drawable.blackknight;
    	}
    	else if( Team == 2 ){
    		gameItems[0] = R.drawable.blackknight;
    		gameItems[row*col-1] = R.drawable.whiteknight;
    	}
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gameItems.length;
	}

	public int getColNumber(){
		return col;
	}
	public int getRowNumber(){
		return row;
	}
	
}

