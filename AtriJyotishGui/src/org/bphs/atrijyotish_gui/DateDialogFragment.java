/*
** Module      : AtriJyotishCalc
** File:       : DateDialogFragment.cpp
** Description : Date dialog
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

//import java.util.Calendar;

import org.bphs.atrijyotish_gui.NativeDataDialog.DateDialogFragmentListener;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

public class DateDialogFragment extends DialogFragment {
	
	public static String TAG = "DateDialogFragment";
	static Context mContext; //I guess hold the context that called it. Needed when making a DatePickerDialog. I guess its needed when conncting the fragment with the context
	static int mYear;
	static int mMonth;
	static int mDay;
	static DateDialogFragmentListener mListener;
	
//	public static DateDialogFragment newInstance(Context context, DateDialogFragmentListener listener, Calendar now) {
	public static DateDialogFragment newInstance(Context context, DateDialogFragmentListener listener, int tYear, int tMonth, int tDay) {
		DateDialogFragment dialog = new DateDialogFragment();
		mContext = context;
		mListener = listener;
		
		mYear  = tYear;			// now.get(Calendar.YEAR);
		mMonth = tMonth-1;		// now.get(Calendar.MONTH);
		mDay   = tDay; 			// now.get(Calendar.DAY_OF_MONTH);
		/*I dont really see the purpose of the below*/
		Bundle args = new Bundle();
		args.putString("title", "Set Date");
		dialog.setArguments(args);//setArguments can only be called before fragment is attached to an activity, so right after the fragment is created
		
		return dialog;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		DatePickerDialog dDlg = new DatePickerDialog(mContext, mDateSetListener, mYear, mMonth, mDay);
		//DatePicker dp = dDlg.getDatePicker();
		//dp.setMinDate(1800);
		return dDlg;
	}
	
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			
			mListener.updateChangedDate(year, monthOfYear, dayOfMonth);
		}
	};
	
	
	
	

}
