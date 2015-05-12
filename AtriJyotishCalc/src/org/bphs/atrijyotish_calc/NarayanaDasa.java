/*
** Module      : AtriJyotishCalc
** File:       : Narayana Dasa.cpp
** Description : Calculation of Narayana dasa
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


package org.bphs.atrijyotish_calc;

import java.util.ArrayList;
//import java.lang.*;
import java.util.Calendar;

import swisseph.SweDate;

public class NarayanaDasa {

	public class sDasaData  {
		public byte  id;
		public int   iPrd;         // in Years for MD; in Months for AD
		public int   startYear, startMonth;
		public int   endYear, endMonth;
		
		public sDasaData(byte id, int iPrd, int startYear, int startMonth, int endYear, int endMonth) {
			this.id = id;
			this.iPrd = iPrd;
			this.startYear  = startYear;
			this.startMonth = startMonth;
			this.endYear    = endYear;
			this.endMonth   = endMonth;
		}
	};

	ChartData pChartData;
	int birthYear, birthMonth, birthDay;
	ArrayList<sDasaData> pDasaData = new ArrayList<sDasaData>();
	int nDasas = 12;
	int nTotalDasas = nDasas * 2;
	int nRasis = 12;
	
	ArrayList<sDasaData> pCurAntarDasaData = new ArrayList<sDasaData>();

	
	public void SetChart(ChartData tChartData) {
		pChartData = tChartData;
		birthDay   = pChartData.gpDay;
		birthMonth = pChartData.gpMonth;
		birthYear  = pChartData.gpYear;
		
		CalculateMahaDasa();
	}
	
	public void CalculateMahaDasa()
	{
		byte Lagna = (byte) pChartData.GetLagnaRasi();
		byte aSign = pChartData.FindStrongestSign(Lagna, (byte)((Lagna+6)%nRasis));  // find strongest of Lagna and 7th house
		
		byte bDirect = 1;
		byte [] incSign = new byte[nDasas];
		
		byte pSign = pChartData.PlanetInSign((byte)(pChartData.P_SA));
		if (pSign == aSign)
		{
			bDirect = 1;
		    for(int i=0;i<nDasas;i++) incSign[i] = bDirect; 
		}
		else {
			pSign = (byte)((aSign + 8) % nRasis);
			if ((pSign % 6) < 3) bDirect = 1;      // Vimsapada 
			else                 bDirect = -1; 	   // Samapada

			// If Ke in Arambha Rasi, reverse the above decision
			pSign = pChartData.PlanetInSign((byte)(pChartData.P_KE));
			if (pSign == aSign)
			{
				bDirect = (byte)(bDirect * (-1));
			}

			// If Lagna is Movable/Fixed/Dual, progression increment changes
			byte dfm = (byte)(Lagna % 3);
			switch(dfm) {
				case 0:  // Movable
				  for(int i=0;i<nDasas;i++) incSign[i] = bDirect; 
				  break;
				  
				case 1: // Fixed
					  for(int i=0;i<nDasas;i++) incSign[i] = (byte)(5 * bDirect); 
					  break;

				case 2: // Dial signs
					  for(int i=0;i<nDasas;i++) incSign[i] = (byte)(4 * bDirect); 
					  incSign[3] = bDirect;
					  incSign[6] = bDirect;
					  incSign[9] = bDirect;
					  break;
			}
		}
		incSign[0] = 0;
		
		// Calculate dasa
		pDasaData.clear();
        int startYear = birthYear;
        int endYear = birthYear;
        
		for(int i=0;i<nDasas;i++) {
			aSign = (byte)((aSign + incSign[i]));
			if (aSign < 0) aSign += nRasis;
			aSign = (byte)(aSign % nRasis);
			int nYears = FindDasaPeriod(aSign);
			endYear = endYear + nYears;
			sDasaData pData = new sDasaData(aSign, nYears, startYear, birthMonth, endYear, birthMonth);
			pDasaData.add(pData);
			startYear = endYear;
		}
		
		// 2nd cycle period = 12 - 1st cycle period
		for(int i=nDasas;i<nTotalDasas;i++) {
			sDasaData pDataOrg = pDasaData.get(i-nDasas);
			
			int nYears = (12 - pDataOrg.iPrd);  // 12 is max period
			endYear = endYear + nYears;
			sDasaData pData = new sDasaData(pDataOrg.id, nYears, startYear, birthMonth, endYear, birthMonth);
			pDasaData.add(pData);
			startYear = endYear;
		}
	}
	
	int FindDasaPeriod(byte dasaSign) {
		byte [] pExalt = {0, 1, 9,  5, 3, 11, 6, 2, 8};
		byte [] pDebil = {6, 7, 3, 11, 9,  5, 0, 8, 2};

		byte bDirect;
		if ((dasaSign % 6) < 3) bDirect = 1; 
		else                    bDirect = -1; 
		
		byte lord1 = (byte)(pChartData.lords1[dasaSign]);
		byte lord2 = (byte)(pChartData.lords2[dasaSign]);
		byte pSign1 = pChartData.PlanetInSign(lord1);
		byte pSign2 = pChartData.PlanetInSign(lord2);
		
		byte dist =0;
		
		if (pSign1 == pSign2) {  // If lord1 = lord2 or both lords in same sign
			dist = (byte)(pSign1 - dasaSign);
			if (bDirect < 0) dist = (byte)(nRasis - dist);
			if (dist <= 0) dist += nRasis;
			if (dist > nRasis) dist -= nRasis;
			
			// Exalt/Debil
			if (pSign1 == pExalt[lord1]) dist++;
			if (pSign1 == pDebil[lord1]) dist--;
			if (lord1 != lord2) {
				if (pSign2 == pExalt[lord2]) dist++;
				if (pSign2 == pDebil[lord2]) dist--;
			}
			if (dist < 0) dist = 0;
			if (dist > 12) dist =12;   // max period of 12 years
		}else {  // Dual lordship
			// if one in dasaSign; other in another sign
			byte pSign = pSign1; // default
			if ( (pSign1 != dasaSign) && (pSign2 != dasaSign)) {
				 pSign = pChartData.FindStrongestSign(pSign1, pSign2);  // find strongest of Lagna and 7th house
				 pChartData.mGlobals.AppendLog("Outside: " + pSign + " : " + pSign1 + " : " + pSign2);
				if (pSign < 0) {  // Equally strong. Take one that gives highest period
					byte dist1 = (byte)(pSign1 - dasaSign);
					if (dist1 <= 0) dist1 += nRasis;
					if (bDirect < 0) dist1 = (byte)(nRasis - dist1);
					byte dist2 = (byte)(pSign2 - dasaSign);
					if (dist2 <= 0) dist2 += nRasis;
					if (bDirect < 0) dist2 = (byte)(nRasis - dist2);

					if (dist1 >= dist2) {
						dist = dist1;
						if (pSign1 == pExalt[lord1]) dist++;
						if (pSign1 == pDebil[lord1]) dist--;
					} else {
						dist = dist2;
						if (pSign2 == pExalt[lord2]) dist++;
						if (pSign2 == pDebil[lord2]) dist--;
					}
				} else {
					byte dist1 = (byte)(pSign - dasaSign);
					if (dist1 <= 0) dist1 += nRasis;
					if (bDirect < 0) dist1 = (byte)(nRasis - dist1);
					dist = dist1;
					pChartData.mGlobals.AppendLog("Dual: " + pSign + " : " + dasaSign + " : " + dist);

					if (pSign == pSign1) {
						if (pSign == pExalt[lord1]) dist++;
						if (pSign == pDebil[lord1]) dist--;
					} else {
						if (pSign == pExalt[lord2]) dist++;
						if (pSign == pDebil[lord2]) dist--;
					}
				}
			}
			else if (pSign1 == dasaSign) { 
				dist = (byte)(pSign2 - dasaSign);
				if (dist <= 0) dist += nRasis;
				if (bDirect < 0) dist = (byte)(nRasis - dist);
				if (pSign2 == pExalt[lord2]) dist++;
				if (pSign2 == pDebil[lord2]) dist--;
			}
			else if (pSign2 == dasaSign) {
				dist = (byte)(pSign1 - dasaSign);
				if (dist <= 0) dist += nRasis;
				if (bDirect < 0) dist = (byte)(nRasis - dist);
				if (pSign1 == pExalt[lord1]) dist++;
				if (pSign1 == pDebil[lord1]) dist--;
			}
	 	}
	    if (dist > 12) dist = 12;   // Max period of 12 years
		return dist;
	}
	
	public ArrayList<sDasaData> GetMahaDasa() {
		return pDasaData;
	}

	public String GetTitle() {
		String tStr = "Narayana dasa: maha dasa\n";
		ArrayList<String> pStr = GetCurrentDasa();
		if (pStr.size() > 0) {
			tStr += pStr.get(0) + " : ";
			tStr += pStr.get(1) + " => ";
			tStr += pStr.get(2);
		}
		return tStr;
	}

	public ArrayList<String> GetCurrentDasa() {
		ArrayList<String> pStr = new ArrayList<String>();
		pStr.clear();

		String tStr="";
        Calendar curDate = Calendar.getInstance();
        SweDate curSD = new SweDate(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), curDate.get(Calendar.DAY_OF_MONTH), 0);
        double curJulDay = curSD.getJulDay();
        
        SweDate sd;
        double startDate, endDate;
        
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
	        sd = new SweDate(pData.startYear, pData.startMonth, birthDay, 0);
	        startDate = sd.getJulDay();
	        sd = new SweDate(pData.endYear, pData.endMonth, birthDay, 0);
	        endDate = sd.getJulDay();
	        
			if  ((curJulDay >= startDate) && (curJulDay <= endDate)) {
				tStr += pChartData.pRasiNames[pData.id] + " => "; 

				ComputeGetAntarDasa((byte)i);
				
				for (int j=0; j<pCurAntarDasaData.size(); j++) {
					sDasaData p1Data = pCurAntarDasaData.get(j);
			        sd = new SweDate(p1Data.startYear, p1Data.startMonth, birthDay, 0);
			        startDate = sd.getJulDay();
			        sd = new SweDate(p1Data.endYear, p1Data.endMonth, birthDay, 0);
			        endDate = sd.getJulDay();
					if  ((curJulDay >= startDate) && (curJulDay <= endDate)) {
						tStr += pChartData.pRasiNames[p1Data.id];
						pStr.add(tStr);
						tStr = String.format("%4d-%02d-%02d", p1Data.startYear, p1Data.startMonth, birthDay);
						pStr.add(tStr);
						tStr = String.format("%4d-%02d-%02d", p1Data.endYear, p1Data.endMonth, birthDay);
						pStr.add(tStr);
						return pStr;
					}
				}
			}
		}
		return pStr;
	}
	
	// Calculate AntarDasa
	public int ComputeGetAntarDasa(byte dasaInd)
	{
		pCurAntarDasaData = new ArrayList<sDasaData>();
		pCurAntarDasaData.clear();

		sDasaData pData = pDasaData.get(dasaInd);
		byte stRasiId = pData.id;
				
		// Find direction direct/reverse: DasaSign Odd/even rasi
		byte iDirect = 1;
		if ((stRasiId % 2) == 1) iDirect = -1;

		// If Both Saturn and Ketu in Arambha rasi, consider one with higher longitude; Ketu from end of sign
		
		
		// If Saturn in Dasa Rasi => Direct
		byte pSign1 = pChartData.PlanetInSign((byte)(pChartData.P_SA));
		
		// If Ketu in Dasa rasi: Opposite
		byte pSign2 = pChartData.PlanetInSign((byte)(pChartData.P_KE));
		
		if (pSign1 == pSign2) {
			if (pSign1 == stRasiId) {
				byte pId = pChartData.GetStronger(pChartData.P_SA, pChartData.P_KE);
				if (pId == pChartData.P_SA) iDirect = 1;
				if (pId == pChartData.P_KE) iDirect = (byte)(iDirect * -1);
			}
		}else {
			if (pSign1 == stRasiId) iDirect = 1;
			if (pSign2 == stRasiId) iDirect = (byte)(iDirect * -1);
		}
		
		// Find Arambha AD
		byte bSign = pChartData.FindStrongestSign(stRasiId, (byte)((stRasiId+6)%nRasis));  // find strongest of Lagna and 7th house
		if (bSign < 0) bSign = stRasiId;
		
		byte lord  = (byte)(pChartData.lords1[bSign]);  //TODO: What will happen in case of dual lordship
		byte aSign = pChartData.gdPlanetSign[lord];
		
		// AD Period
		int nMonths = pData.iPrd;   // NYears/12 = nMonths
		
	    byte stId = aSign;
	    int endYear, endMonth;
	    int startYear = pData.startYear;
	    int startMonth = pData.startMonth;

		for(int i = 0; i< nDasas;i++)
		{
			endYear = startYear;
			endMonth = startMonth + nMonths;
			if (endMonth > 12) {        // max number of months in a year
				endMonth -= 12;
				endYear++;
			}
			sDasaData pData1 = new sDasaData(stId, nMonths, startYear, startMonth, endYear, endMonth);
			startYear = endYear;
			startMonth = endMonth;
			pCurAntarDasaData.add(pData1);

			stId += iDirect;
			if (stId < 0) stId += nRasis;
			stId = (byte)(stId % nRasis);
		}
		return 0;
	}
	

	public ArrayList<String> GetMahaDasaString() {
		ArrayList<String> pStr = new ArrayList<String>();
		pStr.clear();
		
		String tStr;
		
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
			
			tStr = String.format("%2s  %2d  %4d-%02d-%02d => %4d-%02d-%02d", pChartData.pRasiNames[pData.id], pData.iPrd, 
					pData.startYear, pData.startMonth, birthDay,
					pData.endYear, pData.endMonth, birthDay);
			pStr.add(tStr);
		}
		
		return pStr;
	}
	
	public ArrayList<String> GetAntarDasaString() {
		ArrayList<String> pStr = new ArrayList<String>();
		pStr.clear();
		
		String tStr;
		for (int i=0; i<pCurAntarDasaData.size(); i++) {
			sDasaData pData = pCurAntarDasaData.get(i);

			tStr = String.format("%2s  %2d  %4d-%02d-%02d => %4d-%02d-%02d", pChartData.pRasiNames[pData.id], pData.iPrd, 
					pData.startYear, pData.startMonth, birthDay,
					pData.endYear, pData.endMonth, birthDay);
			
			pStr.add(tStr);
		}
		
		return pStr;
	}

}
