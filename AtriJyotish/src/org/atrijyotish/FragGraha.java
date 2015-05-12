/*
** Module      : AtriJyotishCalc
** File:       : FragGraha.cpp
** Description : Fragment to display planet and Lagna Information
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;



public class FragGraha extends FragBase {
    View rootView;
    ListView planetData;
    
	ListViewAdopter<String> listAdapter ;  // To display Planet Info in a list
    ArrayList<String> pPlanetStr;          //            Planet Info

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        rootView = inflater.inflate(R.layout.atri_graha, container, false);
        onCreateInit();

		listAdapter = new ListViewAdopter<String>(getActivity(), R.layout.simplerow, hardwareType);

        planetData = (ListView)rootView.findViewById(R.id.PlanetData);

        return rootView;
    }
    
    public void SetPlanetInfo(ArrayList<String> tList) {
    	pPlanetStr = tList;

    	listAdapter.clear();
		for (int i=0;i<pPlanetStr.size();i++)
			listAdapter.add(pPlanetStr.get(i));
		
		planetData.setAdapter(listAdapter);
    }
    
}
