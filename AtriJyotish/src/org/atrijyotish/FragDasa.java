/*
** Module      : AtriJyotishCalc
** File:       : FragDasa.cpp
** Description : Fragment to display Vimsottari dasa
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

import org.bphs.atrijyotish_gui.*;
import org.bphs.atrijyotish_calc.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;



public class FragDasa extends FragBase {
    View rootView;
    TextView dasaTitle;
    ListView antarDasa;
    ListView SelectedDasa;
    
    VimsottariDasa pVDasa;
	ListViewAdopter<String> vDasaAntarDasaAdapter ;  // To display Vimsottari dasa: Antar dasa of selected dasa
    ArrayList<String> pAntarDasaStr;                 //            Antar dasa of selected MD/AD
	ListViewAdopter<String> vDasaMainDasaAdapter ;   // To display Vimsottari dasa: Selected Dasas
    ArrayList<String> pSelDasaStr;                   //            Selected dasas/AD
    int iLevel;           // Current level of dasa selected
    int    [] curpId;     // Seq of Planets of current antar dasas
    double [] curPrd;     // Period of current antar dasas
    double [] curStDate;  // Start dates of current antar dasas
    int nDasas; 
    ArrayList<String> sCurDasa;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        rootView = inflater.inflate(R.layout.atri_vdasa, container, false);
        onCreateInit();

		vDasaMainDasaAdapter  = new ListViewAdopter<String>(getActivity(), R.layout.simplerow,hardwareType);
		vDasaAntarDasaAdapter = new ListViewAdopter<String>(getActivity(), R.layout.simplerow, hardwareType);
		pVDasa = new VimsottariDasa();
		pSelDasaStr = new ArrayList<String>();
	    sCurDasa = new ArrayList<String>(); sCurDasa.clear();

        dasaTitle =(TextView)rootView.findViewById(R.id.DasaName);
        antarDasa =(ListView)rootView.findViewById(R.id.AntarDasa);

        ViewGroup.LayoutParams pParams;
		pParams = antarDasa.getLayoutParams();
		if (hardwareType > 2) {
			pParams.height = 410;  // To verify on 7" Tablets
			antarDasa.setLayoutParams(pParams);
		}
        
        antarDasa.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) 
            {
                TextView selRow = (TextView)view.findViewById(R.id.rowTextView);
                pSelDasaStr.add(selRow.getText().toString());
                iLevel++;
                UpdateCurrentAD(position);
                UpdateCurrentSelDasa();
            }
        });

        antarDasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) 
            {
            	// NOT USED
//                title.setText("Click : SelectedListener(new Adapt" + position + String.valueOf(i));  
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        SelectedDasa =(ListView)rootView.findViewById(R.id.SelectedDasa);
                
		SelectedDasa.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) 
            {
            	iLevel = 0;
            	pSelDasaStr.clear();
            	UpdateCurrentAD(0);  // it always displays Back to Mahadasa
                UpdateCurrentSelDasa();

                // To get text from selected row of list box
                //TextView myPhone = (TextView)view.findViewById(R.id.rowTextView);
                //title.setText("Click : test data SelectDasa " + myPhone.getText().toString());  
            }
        });

		SelectedDasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) 
            {
                //title.setText("Click : SelectedListener(new Adapt" + position + String.valueOf(i));  
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        return rootView;
    }

    public void SetVDasa(VimsottariDasa tvDasa) { 
    	pVDasa = tvDasa;
        String tStr = pVDasa.GetTitle();
	    mGlobals.AppendLog("FragDasa: "+tStr);
        
        dasaTitle.setText(tStr);
        antarDasa.setItemsCanFocus(true);
    	
	    //mGlobals.AppendLog("FragDasa:1");

    	pAntarDasaStr =  pVDasa.GetMahaDasaString();
	    //mGlobals.AppendLog("FragDasa:2");

    	nDasas = pAntarDasaStr.size();
    	curpId = pVDasa.GetDasaPlanetIds();
    	curPrd = pVDasa.GetDasaPeriods();
    	curStDate = pVDasa.GetDasaStartDates();
    	iLevel = 0;
	    //mGlobals.AppendLog("FragDasa:3");

    	pSelDasaStr.clear();

        vDasaAntarDasaAdapter.clear();
		for (int i=0;i<pAntarDasaStr.size();i++)
			vDasaAntarDasaAdapter.add(pAntarDasaStr.get(i));
		
        antarDasa.setAdapter(vDasaAntarDasaAdapter);
	    //mGlobals.AppendLog("FragDasa:4");

        vDasaMainDasaAdapter.clear();
		for (int i=0;i<pSelDasaStr.size();i++)
			vDasaMainDasaAdapter.add(pSelDasaStr.get(i));
		
		SelectedDasa.setAdapter(vDasaMainDasaAdapter);
	    //mGlobals.AppendLog("FragDasa:5");
    }
    
    private void UpdateCurrentAD(int stId) {
    	if (iLevel == 0) {
    		pAntarDasaStr =  pVDasa.GetMahaDasaString();
        	curpId = pVDasa.GetDasaPlanetIds();
        	curPrd = pVDasa.GetDasaPeriods();
        	curStDate = pVDasa.GetDasaStartDates();
    	}
    	else {
    		pVDasa.ComputeGetAntarDasa(iLevel, curpId[stId],  curPrd[stId], curStDate[stId]);
    		pAntarDasaStr =  pVDasa.GetAntarDasaString();
        	curpId = pVDasa.GetAntarDasaPlanetIds();
        	curPrd = pVDasa.GetAntarDasaPeriods();
        	curStDate = pVDasa.GetAntarDasaStartDates();
    	}
    	
        vDasaAntarDasaAdapter.clear();
		for (int i=0;i<pAntarDasaStr.size();i++)
			vDasaAntarDasaAdapter.add(pAntarDasaStr.get(i));
    }		
    
    private void UpdateCurrentSelDasa() {
        //View page = inflater.inflate(R.layout.vimsottari_dasa, null);
        ListView SelectedDasa =(ListView)rootView.findViewById(R.id.SelectedDasa);
        
        vDasaMainDasaAdapter.clear();
		for (int i=0;i<pSelDasaStr.size();i++)
			vDasaMainDasaAdapter.add(pSelDasaStr.get(i));

		SelectedDasa.setAdapter(vDasaMainDasaAdapter);
    }
    

}
