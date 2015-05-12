/*
** Module      : AtriJyotishCalc
** File:       : InfoDialog.cpp
** Description : Help/About dialog display
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



import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.content.Context;


public class InfoDialog extends DialogFragment {
	 private Button btnOK;
	 
	 public InfoDialog() {
	     // Empty constructor required for DialogFragment
	 }
	
	 public static InfoDialog newInstance(Context tct, String title, int dlgId) {
		 InfoDialog frag = new InfoDialog();
	     Bundle args = new Bundle();
	     args.putString("title", title);
	     args.putInt("Id", dlgId);
	     frag.setArguments(args);
	     return frag;
	 }
	
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		 int dlgId = getArguments().getInt("Id");
	     View view     = inflater.inflate(dlgId, container);

	     getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
	     getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
	     
	     String title = getArguments().getString("title", "Enter Name");
	     getDialog().setTitle(title);
	     
	     btnOK = (Button) view.findViewById(R.id.btnOK);
	     btnOK.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	             getDialog().dismiss();
	         }
	     });
	     
	     return view;
	 }
}


