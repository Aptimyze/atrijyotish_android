/*
** Module      : AtriJyotishCalc
** File:       : FragChart.cpp
** Description : Chart display fragment
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

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;


public class FragChart extends FragBase {
	Settings pSettings;
	ChartDisplay cd;
	NativeData pNative;
    ChartData chartData;
    VimsottariDasa pVDasa;
    ArrayList<String> pCurDasa;
    String pChartName = " ";

    View rootView;
    FrameLayout pCont;

    public FragChart(Settings tSettings) {
    	pSettings = tSettings;
    }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current game state
		pNative.SaveXML(dataPath + "ChartCurrentState.xml");

		// Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.atri_chart, container, false);

        onCreateInit();

        pNative = new NativeData();
		chartData = new ChartData(pSettings);
		//chartData.SetDataPath(dataPath);
		//chartData.Init(pSettings);
		
	    pCurDasa = new ArrayList<String>(); pCurDasa.clear();
		pVDasa = new VimsottariDasa();

	    pCont = (FrameLayout)rootView.findViewById(R.id.chart);    
		cd = new ChartDisplay(rootView.getContext());
		cd.SetHardwareType(hardwareType);  // TODO: hardwareType
        pCont.addView(cd);

        //Set to full screen size
        DisplayMetrics dm = container.getContext().getResources().getDisplayMetrics();
        cd.SetFrameSize(dm.widthPixels, dm.heightPixels);
       
    	DisplayChart();
		//mGlobals.AppendLog("FragChart: OnCreateView: end");
    	
        return rootView;
    }

    public void CalculateChart(NativeData pNative, ChartData cData, VimsottariDasa pVDasa) {
    	cData.SetName(pNative.mName);
        cData.SetDate(pNative.mYear, pNative.mMonth, pNative.mDay);
        cData.SetTime(pNative.mHour, pNative.mMinute, pNative.mSec);
        cData.SetPlace(pNative.mPlace, pNative.mLongD, pNative.mLongM, pNative.mLongS, pNative.mLongEW, 
        		pNative.mLatD, pNative.mLatM, pNative.mLatS, pNative.mLatNS, 
        		pNative.mTzH, pNative.mTzM, pNative.mTzEW);

        //mGlobals.AppendLog(String.format("FragChart: Tz: %d %d", pNative.mTzH, pNative.mTzM));
        
        cData.compute();  			// Calc chart data
        
    	double pMoon = cData.getMoonPos();		// Update V.Dasa
    	double pJulDay = cData.getJulDay();
    	pVDasa.CalculateMahaDasa(pMoon, pJulDay);
    }		

    public void DisplayChart() {
    	if (pChartName.contains("xml")) {
    	   pNative.LoadXML(ct, pChartName);
       	   //mGlobals.AppendLog("FragChart: load" + pChartName);
    	}
		CalculateChart(pNative, chartData, pVDasa);
		pCurDasa = pVDasa.GetCurrentDasa();
		
	    String [] tCurDasa = new String[pCurDasa.size()*1];
	    for (int i=0;i<pCurDasa.size();i++) 
	    	tCurDasa[i] = pCurDasa.get(i);
	
	    ChartData [] pChartData = new ChartData[1];
	    pChartData[0] = chartData;

	    cd.SetChartData(pChartData, tCurDasa);
	    cd.invalidate();
	}
    
    public void SetChart(String chartName) {
    	pNative.LoadXML(ct, chartName);
    	pChartName = chartName;
    	//mGlobals.AppendLog("FragChart: " + pChartName);
    	DisplayChart();
    }

    public void SetChart(NativeData tNative) {
    	pNative = tNative;
    	pChartName = "";
    	DisplayChart();
    }

    public ChartData GetChart() {
    	return chartData;
    }
    
    public ArrayList<String> GetPlanetInfo()
    {
    	return chartData.getPlanetInfo();
    }
    
    public VimsottariDasa GetVDasa() {
    	return pVDasa;
    }
    
    // Call this function after calculating the chart data  (used by AtriMatch)
    public void SetChart(ChartData [] pChartData, String [] tCurDasa) {
	    cd.SetChartData(pChartData, tCurDasa);
	    cd.invalidate();
    }    
}
