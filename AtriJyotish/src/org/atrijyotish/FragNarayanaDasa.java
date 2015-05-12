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


public class FragNarayanaDasa extends FragBase {
    View rootView;
    TextView dasaTitle;
    TextView AntardasaName;
    ListView mahaDasa;
    ListView antarDasa;
    
    NarayanaDasa pNDasa;
	ListViewAdopter<String> nDasaMahaDasaAdapter ;   // To display Narayana dasa: Maha Dasas
    ArrayList<String> pMahaDasaStr;                   //            
	ListViewAdopter<String> nDasaAntarDasaAdapter ;  // To display Narayana dasa: Antar dasa of selected dasa
    ArrayList<String> pAntarDasaStr;                 //            Antar dasa of selected MD/AD
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        rootView = inflater.inflate(R.layout.atri_narayana_dasa, container, false);
        onCreateInit();

		nDasaMahaDasaAdapter  = new ListViewAdopter<String>(getActivity(), R.layout.simplerow,hardwareType);
		nDasaAntarDasaAdapter = new ListViewAdopter<String>(getActivity(), R.layout.simplerow, hardwareType);
		pNDasa = new NarayanaDasa();
		pAntarDasaStr = new ArrayList<String>();

        dasaTitle = (TextView)rootView.findViewById(R.id.DasaName);
        antarDasa = (ListView)rootView.findViewById(R.id.AntarDasa);
        mahaDasa  = (ListView)rootView.findViewById(R.id.MahaDasa);
        AntardasaName = (TextView)rootView.findViewById(R.id.AntardasaName);
        
        mahaDasa.setOnItemClickListener(new AdapterView.OnItemClickListener() 
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) 
            {
                TextView selRow = (TextView)view.findViewById(R.id.rowTextView);
                AntardasaName.setText("AD of: " + selRow.getText().toString());
                UpdateCurrentAD(position);
            }
        });

        mahaDasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
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

        return rootView;
    }

    public void SetNarayanaDasa(NarayanaDasa tnDasa) { 
    	pNDasa = tnDasa;
        String tStr = pNDasa.GetTitle();
        dasaTitle.setText(tStr);
        mahaDasa.setItemsCanFocus(true);
	    //mGlobals.AppendLog("FragDasa: "+tStr);
    	
    	pMahaDasaStr =  pNDasa.GetMahaDasaString();
    	
        nDasaMahaDasaAdapter.clear();
		for (int i=0;i<pMahaDasaStr.size();i++)
			nDasaMahaDasaAdapter.add(pMahaDasaStr.get(i));
		
		mahaDasa.setAdapter(nDasaMahaDasaAdapter);

		pAntarDasaStr.clear();

        nDasaAntarDasaAdapter.clear();
		for (int i=0;i<pAntarDasaStr.size();i++)
			nDasaAntarDasaAdapter.add(pAntarDasaStr.get(i));
		
        antarDasa.setAdapter(nDasaAntarDasaAdapter);
    }
    
    private void UpdateCurrentAD(int stId) {
		pNDasa.ComputeGetAntarDasa((byte)stId);
		pAntarDasaStr =  pNDasa.GetAntarDasaString();
    	
        nDasaAntarDasaAdapter.clear();
		for (int i=0;i<pAntarDasaStr.size();i++)
			nDasaAntarDasaAdapter.add(pAntarDasaStr.get(i));
    }		
}
