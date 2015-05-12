/*
** Module      : AtriJyotishCalc
** File:       : FragBase.cpp
** Description : Fragment base class to manage common
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

import org.bphs.atrijyotish_calc.Globals;

//import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
//import android.view.inputmethod.InputMethodManager;

public class FragBase  extends Fragment{
	public Context ct;
	public Globals mGlobals;
	public String dataPath, subPath;
	public int hardwareType = 2;    // 1=Small (< 4"); 2 Normal (4-5"); 3 Large (7"); 4 XLarge (10") // TODO
    public InputMethodManager imm;

    public void onCreateInit() {
        setHasOptionsMenu(true);

        setRetainInstance(true);
        ct = getActivity().getApplicationContext();
	    Bundle args = getArguments();
	    dataPath = args.getString("path");
	    subPath = args.getString("subpath");
		mGlobals = new Globals(ct, dataPath + "/log/");
		hardwareType = args.getInt("hType");
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
