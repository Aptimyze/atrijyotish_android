/*
** Module      : AtriJyotishCalc
** File:       : NativeDataDialog.cpp
** Description : To manage native data
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

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
//import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.widget.Toast;





import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.bphs.atrijyotish_calc.*;
import org.bphs.atrijyotish_gui.PlaceSelectDialog.PlaceSelectDialogListener;


public class NativeDataDialog  extends View  implements PlaceSelectDialogListener {
//	public Globals mGlobals;
	Context ct;
	View page;
	NativeData pNative;
	String pDataPath;
	
	public EditText editName;
	EditText editPlace,editCountry, editState;
	EditText editLatitude, editLongitude, editTz;
	Button  btnDate, btnTime, btnCalc;
	ImageButton btnView;
	RadioGroup editGender;
	TextView txtErrMsg;
	
	Calendar now;
	DateDialogFragment frag;  // DatePicker
	Activity pActivity;
	FragmentManager pFm;
	Context ctApp;
	MyTimePickerDialog.OnTimeSetListener pTSL;
	
	//TOTEST
	static NativeDataDialogListener pListner;

	public interface NativeDataDialogListener {
	    void onFinishNativeDataDialog(boolean bSelect, NativeData pNative);
	}
	//TOTEST
	
	public NativeDataDialog(Context tctApp, FragmentManager fm, View tPage, LayoutInflater inflater, final NativeDataDialogListener tListner) {
		super(tPage.getContext());
        //page = inflater.inflate(org.bphs.atrijyotish_gui.R.layout.atri_dtp, null);
		page = tPage;
		ct = page.getContext();
		pFm = fm;
		ctApp = tctApp;

		pListner = tListner;

	    now = Calendar.getInstance();

        editName=(EditText)page.findViewById(R.id.editName);
        editGender = (RadioGroup)page.findViewById(R.id.editGender);
        editPlace=(EditText)page.findViewById(R.id.editPlace);
        editState=(EditText)page.findViewById(R.id.editState);
        editCountry=(EditText)page.findViewById(R.id.editCountry);
        //btnView=(Button)page.findViewById(R.id.btnView);
        btnView=(ImageButton)page.findViewById(R.id.btnPlace);
        editLatitude=(EditText)page.findViewById(R.id.editLatitude);
        editLongitude=(EditText)page.findViewById(R.id.editLongitude);
        editTz=(EditText)page.findViewById(R.id.editTz);
        txtErrMsg = (TextView)page.findViewById(R.id.txtErrMsg);
        
        // Date :---------------------------------------------------------
        btnDate = (Button)page.findViewById(R.id.btnDate);
        
        btnDate.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
            	FragmentTransaction ft = pFm.beginTransaction(); //get the fragment
            	frag = DateDialogFragment.newInstance(ct, new DateDialogFragmentListener(){
            		public void updateChangedDate(int year, int month, int day){
            			String tStr = String.format("%04d-%02d-%02d", year, (month+1), day);
            			btnDate.setText(tStr);
            			pNative.SetDate(tStr);
            			now.set(year, month, day);
            		}
            	}, pNative.mYear, pNative.mMonth, pNative.mDay);
            	
            	frag.show(ft, "DateDialogFragment");
            	
        	}
        });

        // Time:---------------------------------------------------------
        btnTime = (Button)page.findViewById(R.id.btnTime);
        btnTime.setOnClickListener(new OnClickListener(){   
            @Override  
            public void onClick(View view) {
            	String tStr = btnTime.getText().toString();
            	String[] strArray = tStr.split(":");
    			now.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strArray[0]));
    			now.set(Calendar.MINUTE, Integer.parseInt(strArray[1]));
    			now.set(Calendar.SECOND, Integer.parseInt(strArray[2]));
        		MyTimePickerDialog mTimePicker = new MyTimePickerDialog(ct, pTSL, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        		mTimePicker.show();		
            };
        });

		pTSL = new MyTimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hour, int minute, int sec) {
    			String tStr = String.format("%02d:%02d:%02d", hour, minute, sec);
				btnTime.setText(tStr);
				pNative.SetTime(tStr);
    			now.set(Calendar.HOUR_OF_DAY, hour);
    			now.set(Calendar.MINUTE, minute);
    			now.set(Calendar.SECOND, sec);
			}
		};

        btnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
	     	    PlaceSelectDialog editDialog = PlaceSelectDialog.newInstance(ct, "Enter (> 3 chars) & CLick Find.\n Select from List", pDataPath+"db", NativeDataDialog.this);
	     	    
            	try{
    	    	    editDialog.show(pFm, "fragment_edit_name1");
        		}catch( Exception es ){
        			System.out.println("Stacktrace "+es.toString());
        				WriteTrace(es.toString());
        			//es.printStackTrace();
        		}
            }
         });
		
		
        // Save DTP
		Button btnSave = (Button) page.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() 
		{
			String m_chosen;
			@Override
			public void onClick(View v) {
				//Create FileSaveDialog and register a callback
				FileDialog FileSaveDialog =  new FileDialog(ctApp, "FileSave", new FileDialog.FileDialogListener()
				{
					@Override
					public void onChosenDir(String chosenDir) 
					{
						m_chosen = chosenDir;
					    //	Toast.makeText(AtriJyotishActivity.this, "File: " + m_chosen, Toast.LENGTH_LONG).show();

						// Set values from form to NativeDTP
						pNative.mName = editName.getText().toString().trim();
						pNative.mGender = editGender.indexOfChild(page.findViewById(editGender.getCheckedRadioButtonId()));
						pNative.mPlace = editPlace.getText().toString().trim();
						pNative.mState = editState.getText().toString().trim();
						pNative.mCountry = editCountry.getText().toString().trim();
						pNative.SetDateTime(btnDate.getText().toString().trim(), btnTime.getText().toString().trim());
						//TODO Validate Lat, Long, Tz
						/*int err = */ pNative.SetLatLongTz(editLatitude.getText().toString().trim(), editLongitude.getText().toString().trim(), editTz.getText().toString().trim());

						//if (err < 0) txtErrMsg.setText("Lat/Long/tz Format error");
						//else txtErrMsg.setText(" ");
						
						pNative.SaveXML(m_chosen);
						
					}
				});
				
				//You can change the default filename using the public variable "Default_File_Name"
				FileSaveDialog.Default_File_Name = editName.getText().toString().trim() + ".xml";  // TODO aj
				FileSaveDialog.chooseFile_or_Dir(pDataPath);
			}
		});

        // FileOpenDialog
		Button btnLoad = (Button) page.findViewById(R.id.btnLoad);
		btnLoad.setOnClickListener(new OnClickListener() 
		{
			String m_chosen;
			@Override
			public void onClick(View v) {
				FileDialog FileOpenDialog =  new FileDialog(ctApp, "FileOpen", new FileDialog.FileDialogListener()
				{
					@Override
					public void onChosenDir(String chosenDir) 
					{
						m_chosen = chosenDir;
						
						pNative.Load(ct, m_chosen);
					//	Toast.makeText(AtriJyotishActivity.this, "Load: " + pNative.sTestStr, Toast.LENGTH_LONG).show();

						//pNative.LoadXML(ct, m_chosen);
						editName.setText(pNative.mName);
				        String tStr = String.format("%04d-%02d-%02d", pNative.mYear, pNative.mMonth, pNative.mDay);
				        btnDate.setText(tStr);
				        tStr = String.format("%02d:%02d:%02d", pNative.mHour, pNative.mMinute, pNative.mSec);
				        btnTime.setText(tStr);
				        editGender.check(editGender.getChildAt(pNative.mGender).getId());
				        editPlace.setText(pNative.mPlace);
				        editState.setText(pNative.mState);
				        editCountry.setText(pNative.mCountry);
				        editLatitude.setText(pNative.sLatitude);
				        editLongitude.setText(pNative.sLongitude);
				        editTz.setText(pNative.sTz);
						//Toast.makeText(AtriJyotishActivity.this, "Test: " + pNative.sTestStr, Toast.LENGTH_LONG).show();
					}
				});
				
				//You can change the default filename using the public variable "Default_File_Name"
				FileOpenDialog.Default_File_Name = "*.xml";   // TODO file ext .aj
				FileOpenDialog.chooseFile_or_Dir(pDataPath);
			}
		});

        // Calculate and display chart
		btnCalc = (Button) page.findViewById(R.id.btnCalc);
		btnCalc.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
					//Read data from DTP; calculate; display Chart
					pNative.mName = editName.getText().toString().trim();
					pNative.mGender = editGender.indexOfChild(findViewById(editGender.getCheckedRadioButtonId()));
					pNative.SetDateTime(btnDate.getText().toString().trim(), btnTime.getText().toString().trim());
					pNative.SetLatLongTz(editLatitude.getText().toString().trim(), editLongitude.getText().toString().trim(), editTz.getText().toString().trim());
					pNative.SetPlace(editPlace.getText().toString().trim(), editState.getText().toString().trim(), editCountry.getText().toString().trim());
					pListner.onFinishNativeDataDialog(true, pNative);
//TODO		        CalculateChart();
			}
		});

		btnCalc.setEnabled(false);
		//btnCalc.setVisibility(View.GONE);
	}

	//TODO
	public void CalcEnable(boolean bEnable) {
		btnCalc.setEnabled(bEnable);
		//btnCalc.setVisibility(View.VISIBLE);
	
	}
	
	public void WriteTrace(String tStr) {
		try {
			FileOutputStream fop = null;
			File file;			 
			file = new File("/sdcard/bluetooth/trace.txt");
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
	
	
	// DatePicker Dialog cllback
    public interface DateDialogFragmentListener{
    	//this interface is a listener between the Date Dialog fragment and the activity to update the buttons date
    	public void updateChangedDate(int year, int month, int day);
    }

    @Override
    public void onFinishPlaceSelectDialog(boolean bSelect, String sPlace, String sState, String sCountry, String sLatitude, String sLongitude, String sTz) {
    	if (bSelect) {
	        editPlace.setText(sPlace);
	        editState.setText(sState);
	        editCountry.setText(sCountry);
	        editLatitude.setText(sLatitude);
	        editLongitude.setText(sLongitude);
	        editTz.setText(sTz);
	
			pNative.SetLatLongTz(sLatitude, sLongitude, sTz);
			pNative.SetPlace(sPlace, sState, sCountry);
    	}
    }

    
    public void SetDbPath(String dataPath) {
    	pDataPath = dataPath;
    }
    
	public void SetNative(NativeData tNative) {
		pNative = tNative;
        // Update GUI from pNative
		editName.setText(pNative.mName);
        editGender.check(editGender.getChildAt(pNative.mGender).getId());
        
        editPlace.setText(pNative.mPlace);
        editState.setText(pNative.mState);
        editCountry.setText(pNative.mCountry);
        editLatitude.setText(pNative.sLatitude);
        editLongitude.setText(pNative.sLongitude);
        editTz.setText(pNative.sTz);

        String tStr = String.format("%04d-%02d-%02d", pNative.mYear, pNative.mMonth, pNative.mDay);
        btnDate.setText(tStr);
        tStr = String.format("%02d:%02d:%02d", pNative.mHour, pNative.mMinute, pNative.mSec);
        btnTime.setText(tStr);
	}
}
