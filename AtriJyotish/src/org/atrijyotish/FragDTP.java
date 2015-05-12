/*
** Module      : AtriJyotishCalc
** File:       : FragDTP.cpp
** Description : Fragment to dialog to enter native DTP
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


package org.atrijyotish;

import org.bphs.atrijyotish_calc.*;
import org.bphs.atrijyotish_gui.*;

import android.os.Bundle;
import android.app.FragmentManager;
//import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;


import org.bphs.atrijyotish_gui.NativeDataDialog.NativeDataDialogListener;

public class FragDTP extends FragBase implements NativeDataDialogListener{
    View rootView;
    NativeData pNative;
	AtriJyotishActivity pAct;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(org.bphs.atrijyotish_gui.R.layout.atri_dtp, container, false);
        onCreateInit();
        setRetainInstance(true);

        pNative = new NativeData();
		pNative.Load(ct, dataPath + "xml" + "/SwamiVivekananda.xml");

    	FragmentManager fm = getActivity().getFragmentManager();
        pAct = (AtriJyotishActivity)getActivity();

        NativeDataDialog ndDlg = new NativeDataDialog(getActivity(), fm, rootView, inflater, this);
        ndDlg.SetDbPath(dataPath);
        ndDlg.SetNative(pNative);
        ndDlg.CalcEnable(true);
		//ndDlg.mGlobals = mGlobals;
		
        ndDlg.editName.clearFocus();
    	imm.hideSoftInputFromWindow(ndDlg.editName.getWindowToken(), 0);
        
        return rootView;
    }

    @Override
    public void onFinishNativeDataDialog(boolean bSelect,  NativeData pNative) {
	    //mGlobals.AppendLog("FragDTP: " + pNative.mName);
		//pAct = (AtriJyotishActivity)getActivity();
    	if (bSelect)
		   pAct.SetChart(pNative);
    }
    
}
