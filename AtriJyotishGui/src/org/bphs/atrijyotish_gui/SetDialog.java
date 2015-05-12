/*
** Module      : AtriJyotishCalc
** File:       : SetDialog.cpp
** Description : Dialog to select settings
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


import org.bphs.atrijyotish_calc.*;

//import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
//import android.app.DialogFragment;
import android.content.Context;
import android.support.v4.app.DialogFragment;


public class SetDialog extends DialogFragment {
     View view;
	 Button btnSave;
     Button btnClose;
     
     static Settings pSettings;

     RadioGroup rgPlanetPos, rgNodes, rgSunDisc;
     Spinner lstAyanamsa;
     ArrayAdapter<String> adAyanamsa;
     List<String> pAyanamsaStr;
	 
	 public SetDialog() {
	     // Empty constructor required for DialogFragment
	 }
	
	 public static SetDialog newInstance(Context tct, String title, int dlgId, Settings tSettings) {
		 SetDialog frag = new SetDialog();
		 pSettings = tSettings;
	     Bundle args = new Bundle();
	     args.putString("title", title);
	     args.putInt("Id", dlgId);
	     frag.setArguments(args);
	     return frag;
	 }
	
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		 int dlgId = getArguments().getInt("Id");
	     view     = inflater.inflate(dlgId, container);

	     getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
	     //getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher); TODO
	     
	     String title = getArguments().getString("title", "Atri Jyotish");
	     getDialog().setTitle(title);
	     
	     rgPlanetPos = (RadioGroup) view.findViewById(R.id.rgPlanetPos);
	     ((RadioButton)rgPlanetPos.getChildAt(pSettings.GetPlanetCalcFlagIndex())).setChecked(true);

	     rgNodes = (RadioGroup) view.findViewById(R.id.rgNodes);
	     ((RadioButton)rgNodes.getChildAt(pSettings.GetNodesFlagIndex())).setChecked(true);

	     rgSunDisc = (RadioGroup) view.findViewById(R.id.rgSunDisc);
	     ((RadioButton)rgSunDisc.getChildAt(pSettings.GetSunDiscFlagIndex())).setChecked(true);
	     
	     lstAyanamsa = (Spinner) view.findViewById(R.id.lstAyanamsa);
	     pAyanamsaStr = pSettings.GetAyanamsaList();
	     int id = pSettings.GetAyanamsaIndex();
	     adAyanamsa = new ArrayAdapter<String>(getActivity(), R.layout.simplerow, pAyanamsaStr);
	     adAyanamsa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     lstAyanamsa.setAdapter(adAyanamsa);
	     lstAyanamsa.setSelection(id);
	     
	     
	     btnSave = (Button) view.findViewById(R.id.btnSave);
	     btnSave.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	        	 // Save in settings and also save file
	        	 
	    	     int id = rgPlanetPos.indexOfChild(view.findViewById(rgPlanetPos.getCheckedRadioButtonId()));
	    	     pSettings.SetPlanetCalcFlagIndex(id);
	    	     
	    	     id = rgNodes.indexOfChild(view.findViewById(rgNodes.getCheckedRadioButtonId()));
	    	     pSettings.SetNodesFlagIndex(id);

	    	     id = rgSunDisc.indexOfChild(view.findViewById(rgSunDisc.getCheckedRadioButtonId()));
	    	     pSettings.SetSunDiscFlagIndex(id);

	    	     id = lstAyanamsa.getSelectedItemPosition();
	    	     pSettings.SetAyanamsaIndex(id);
	    	     
	    	     pSettings.Save();
	    	     
	             getDialog().dismiss();
	         }
	     });

	     btnClose = (Button) view.findViewById(R.id.btnClose);
	     btnClose.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	             getDialog().dismiss();
	         }
	     });

	     return view;
	 }
}

