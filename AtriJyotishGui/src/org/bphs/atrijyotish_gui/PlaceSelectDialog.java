/*
** Module      : AtriJyotishCalc
** File:       : PlaceSelectDialog.cpp
** Description : Dialog to select place
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

import android.app.DialogFragment;
import android.widget.EditText;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Gravity;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;





//import android.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
//import android.graphics.Color;
import android.content.Context;


public class PlaceSelectDialog extends DialogFragment {

 private EditText mEditPlace, mEditState, mEditCountry, mEditLatitude, mEditLongitude, mEditTz;
 private ImageButton mBtnFind;
 
 SQLiteDatabase dbPlaces;
 Spinner showPlaces;
 List<String> listPlaces;
 Cursor cPlace;
 ArrayAdapter<String> listPlacesAdapter ;
 private static String pPath;
 
 static PlaceSelectDialogListener pListner;
 
 public interface PlaceSelectDialogListener {
     void onFinishPlaceSelectDialog(boolean bSelect, String sPlace, String sState, String sCountry, String sLatitude, String sLongitude, String sTz);
 }

 public PlaceSelectDialog() {
     // Empty constructor required for DialogFragment
 }

 public static PlaceSelectDialog newInstance(Context tct, String title, String path, final PlaceSelectDialogListener tListner) {
	 pPath = path;
	 pListner = tListner;
	 
	 PlaceSelectDialog frag = new PlaceSelectDialog();
     Bundle args = new Bundle();
     args.putString("title", title);
     frag.setArguments(args);
     return frag;
 }

 @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     View view     = inflater.inflate(R.layout.atri_place_db_dialog, container);
     mEditPlace    = (EditText) view.findViewById(R.id.editPlace);
     mEditState     = (EditText) view.findViewById(R.id.editState);
     mEditCountry   = (EditText) view.findViewById(R.id.editCountry);
     mEditLatitude  = (EditText) view.findViewById(R.id.editLatitude);
     mEditLongitude = (EditText) view.findViewById(R.id.editLongitude);
     mEditTz        = (EditText) view.findViewById(R.id.editTz); 
     mBtnFind        = (ImageButton) view.findViewById(R.id.btnFind); 
 
     mEditPlace.setText("Tirumala");  // Default place
     
     String path = pPath + "/AstroOpenSourceAtlas.db";
//     path = ct.getFilesDir() + File.separator + "/db" + "/AstroOpenSourceAtlas.db";
     
     dbPlaces = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

     listPlaces = new ArrayList<String>();
     listPlaces.clear();
     listPlaces.add("Select Place");
	
  	 listPlacesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listPlaces);
	 listPlacesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     
     showPlaces=(Spinner) view.findViewById(R.id.showPlaces);
	 UpdatePlaceList(mEditPlace.getText().toString());  // Update ShowPlaces List with default place name
 	 showPlaces.setAdapter(listPlacesAdapter);

 	mBtnFind.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
         	String tPlace = mEditPlace.getText().toString().trim();
     		if(tPlace.length() < 3)
     		{
     			//showMessage("Error", "Please enter Place atleast 2 chars");
     			return;
     		}

     		UpdatePlaceList(tPlace);

         }
      });

     showPlaces.setClickable(true);
	 
     showPlaces.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
         public  void  onItemSelected(AdapterView<?> parent, View view, int  position, long  id) { 
 	            // On selecting a spinner item
 	            //String item = parent.getItemAtPosition(position).toString();
 	            // Showing selected spinner item
 	            //Toast.makeText(parent.getContext(), "Selected: " + item + "pos: " + position, Toast.LENGTH_LONG).show();
         		if (position < 1) return;
         		
 	            mEditPlace.setText((String)(listPlacesAdapter.getItem(position)));

 	            cPlace.moveToPosition(position-1);

         		Cursor cCountry=dbPlaces.rawQuery("SELECT * FROM country WHERE id = '" + cPlace.getString(2) + "'", null);
         		cCountry.moveToFirst();
         		if (cCountry.getCount() >0)
         		   mEditCountry.setText(cCountry.getString(1));
         		else
          		   mEditCountry.setText(cPlace.getString(2));
         		
         		// State
         		Cursor cState=dbPlaces.rawQuery("SELECT * FROM state WHERE id = '" + cPlace.getString(3) + "'", null);
         		cState.moveToFirst();
         		if (cState.getCount() >0)
         		   mEditState.setText(cState.getString(1));
         		else
          		   mEditState.setText(cPlace.getString(3));

         		// Latitude
         		mEditLatitude.setText(String.format("%03d:%02d:%02d:%1s", cPlace.getInt(4), cPlace.getInt(5), cPlace.getInt(6),cPlace.getString(7) ));
         		mEditLongitude.setText(String.format("%03d:%02d:%02d:%1s", cPlace.getInt(8), cPlace.getInt(9), cPlace.getInt(10),cPlace.getString(11) ));
         		
         		// Time Zone
         		Cursor cTz=dbPlaces.rawQuery("SELECT * FROM timezone WHERE id = '" + cPlace.getString(13) + "'", null);
         		cTz.moveToFirst();
         		if (cTz.getCount() >0) {
         			String tStr1;
         			if (cTz.getInt(3) == 1) tStr1 = "E";
         			else                    tStr1 = "W";
	            	   mEditTz.setText(String.format("%03d:%02d:%1s", cTz.getInt(1), cTz.getInt(2), tStr1 ));
         		}
         		else
         			mEditTz.setText("000:00:E");
         } 
     
         public  void  onNothingSelected(AdapterView<?> parent) { 
         } 
     });  
	 
     String title = getArguments().getString("title", "Select Place");
     String pTitle = "<b><font color='#FF7F27'>" + title + "</font></b>";
     getDialog().setTitle(Html.fromHtml(pTitle));
     TextView textView = (TextView) this.getDialog().findViewById(android.R.id.title);
     if(textView != null)
     {
         textView.setGravity(Gravity.CENTER);
     }
     // Show soft keyboard automatically
     //mEditText.requestFocus();
     //mEditText.setOnEditorActionListener(this);
     //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

     Button bCancel = (Button) view.findViewById(R.id.btnCancel);
     bCancel.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
        	 pListner.onFinishPlaceSelectDialog(false, "Canceled", "", "","","","");
             dbPlaces.close();
             dbPlaces = null;
             getDialog().dismiss();
         }
     });
     
     Button bSelect = (Button) view.findViewById(R.id.btnSelect);
     bSelect.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
	     	    
         	try{
         		pListner.onFinishPlaceSelectDialog(true, mEditPlace.getText().toString(),
	            		 mEditState.getText().toString(), mEditCountry.getText().toString(),
	            		 mEditLatitude.getText().toString(), mEditLongitude.getText().toString(),
	            		 mEditTz.getText().toString());
	             getDialog().dismiss();
     		}catch( Exception es ){
     			System.out.println("Stacktrace "+es.toString());
     				WriteTrace(es.toString());
     			//es.printStackTrace();
     		}
        	 
         }
     });
     
     return view;
 }
 
	public void UpdatePlaceList(String tPlace) {
 		if (tPlace.length() < 3)
           return;
 		
		cPlace=dbPlaces.rawQuery("SELECT * FROM place WHERE name LIKE '"+tPlace+"%'", null);
	
		listPlaces.clear();
		
		if (cPlace.getCount() <= 0) {
            //Toast.makeText(getApplicationContext(), "Selected: " + tPlace, Toast.LENGTH_LONG).show();

			return;
		}
		
		if(cPlace.moveToFirst())
		{
			String tStr = cPlace.getString(1);
			mEditPlace.setText(tStr);
	
			Cursor cCountry=dbPlaces.rawQuery("SELECT * FROM country WHERE id = '" + cPlace.getString(2) + "'", null);
			cCountry.moveToFirst();
			if (cCountry.getCount() >0)
			   mEditCountry.setText(cCountry.getString(1));
			else
	 		   mEditCountry.setText("Not found");;
	 		
   	 		listPlaces.add("Select Place");
	 		   
	 		listPlaces.add(cPlace.getString(1)); // + ":" + cPlace.getString(2));
			while (cPlace.moveToNext()) {
				listPlaces.add(cPlace.getString(1));  // + ":" + cPlace.getString(2));
			}
   			showPlaces.setSelection(0, true);
   			showPlaces.setSelection(1, true);
		}
		else
		{
			//showMessage("Error", "Place not found");
			//clearText();
		}
	}


	public void WriteTrace(String tStr) {
		try {
			FileOutputStream fop = null;
			File file;			 
			file = new File("/sdcard/bluetooth/trace1.txt");
			fop = new FileOutputStream(file);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = tStr.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


