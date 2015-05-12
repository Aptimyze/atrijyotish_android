/*
** Module      : AtriJyotishCalc
** File:       : ListViewAdopter.cpp
** Description : Base list box adopter
**
** Contact     : AstroOpenSource@gmail.com
**
** Web site    : http://AtriJyotishAndroid.sourceforge.net/
** Download    : http://sourceforge.net/projects/atrijyotishandroid/
**
** This software is provided "as is", under GNU public license with 
** NO WARRANTY OF ANY KIND, either express or implied.
** No author or distributor accepts any responsibility for the consequences of using it,
** or for whether it serves any particular purpose or works at all. In no event shall   
** the authors or copyright holders be liable for any claim, damages or 
** other liability, arising from the use or performance of this software.
**
*/



package org.bphs.atrijyotish_gui;

import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.graphics.Color;


public class ListViewAdopter<E> extends ArrayAdapter<E>{
	public int textSize = 15;
	private LayoutInflater layoutInflater;
	View view;
	int hType; 
	
    public ListViewAdopter(Context context, int textViewResourceId, E[] objects) {
        super(context, textViewResourceId, objects);
    }
    public ListViewAdopter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
		layoutInflater = LayoutInflater.from(context);
    }

    public ListViewAdopter(Context context, int textViewResourceId, int hardwareType) {
        super(context, textViewResourceId);
		layoutInflater = LayoutInflater.from(context);
		hType = hardwareType;
		if (hType > 2) textSize = 25;
    }

    public View getView(int position, View convertView, ViewGroup parent){
    	if(convertView == null){
 			convertView = layoutInflater.inflate(R.layout.simplerow, null);
 			TextView pRow = (TextView) convertView.findViewById(R.id.rowTextView);
 			pRow.setTextColor(Color.BLUE);
    		pRow.setTextSize(textSize);

   		    view = super.getView(position, convertView, parent);

		    // NOTE: textSize is set in the custom adapter's constructor
		    // int textSize
   		    //tvRow.setTextSize(textSize);
   		    //convertView.invalidate();
    		
        	//View view = super.getView(position, convertView, parent); 
    	}    	
    	int pos = (position % 2);
    	if (pos > 0)
            view.setBackgroundColor(Color.WHITE);
    	else
            view.setBackgroundColor(Color.LTGRAY);
    	
        return super.getView(position, convertView, parent);
	}
}



/*
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent" >
    

    <ListView
       android:id="@+id/PlanetData"
       	  android:layout_x="10dp"
		  android:layout_y="10dp"
          android:layout_width="400dp"
          android:layout_height="600dp"
       	  android:ems="10"
       	  android:gravity="left|top"
          android:inputType="textMultiLine" />
       
</RelativeLayout>
*/