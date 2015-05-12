/*
** Module      : AtriJyotishCalc
** File:       : Vimsottari Dasa.cpp
** Description : Calculation of Vimsottari dasa
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

public class VimsottariDasa {

	public class sDasaData  {
		public int     level;
		public int     id;
		public double  dPrd;
		public double  startDate;
		public double  endDate;
		
		public sDasaData(int level, int id, double dPrd, double startDate, double endDate) {
			this.level = level;
			this.id = id;
			this.dPrd = dPrd;
			this.startDate = startDate;
			this.endDate = endDate;
		}
	};

	int P_SU=0, P_MO=1, P_MA=2, P_ME=3, P_JU=4, P_VE=5, P_SA=6, P_RA=7, P_KE=8;
	
	int [] pIdToDaySeq  = {P_KE, P_VE, P_SU, P_MO, P_MA, P_RA, P_JU, P_SA, P_ME};
	String[] pPlanetStr = {"Su", "Mo", "Ma", "Me", "Ju", "Ve", "Sa", "Ra", "Ke"};
	
	// pIdToDasaSeq position of each planet in pIdToDaySeq: Sun (pIdToDasaSeq[0]) is in pIdToDaySeq[2]
	int [] pIdToDasaSeq = {2, 3, 4, 8, 6, 1, 7, 5, 0};  
	int dasaSeqStart = 0;  // AntarDasa Starts from Dasa lord itself
	int dasaSeqDir   = 1;  // Forward direction

	Double pMoonPos;
	double planetPrd[] = {7.0, 20.0, 6.0, 10.0, 7.0, 18.0, 16.0, 19.0, 17.0};  // Period in years
    double totalPeriod = 120.0;
    double paramAyus = 120.0;
	double totalAyus   = 120.0;
	int nDasas = 9;

    double passedPeriod;
    double moonDasaLeft;
    int    startId;
	int nLevels     = 5;  // Max levels of dasa (MD, AD, PD, SD, PAD, DAD)
	double daysPerYear = 365.2425;  // TODO : Move to settings

	
	double  dasaRemain;
	int     nDasa;               // Contains Number of Dasa
	int     nLevel;              // Contains Number of Levels
	
	ArrayList<sDasaData> pDasaData = new ArrayList<sDasaData>();
	ArrayList<sDasaData> pCurAntarDasaData = new ArrayList<sDasaData>();
	
	public ArrayList<sDasaData> GetMahaDasa() {
		return pDasaData;
	}

	
	public String GetTitle() {
		String tStr = "Vimsottari dasa: ";
		ArrayList<String> pStr = GetCurrentDasa();
		if (pStr.size() > 0) {
			tStr += pStr.get(0) + "  ";
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
        
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
			if  ((curJulDay >= pData.startDate) && (curJulDay <= pData.endDate)) {
				tStr += pPlanetStr[pData.id] + "=>"; 

				ComputeGetAntarDasa(1, pData.id, pData.dPrd, pData.startDate);
				for (int j=0; j<pCurAntarDasaData.size(); j++) {
					sDasaData p1Data = pCurAntarDasaData.get(j);
					if  ((curJulDay >= p1Data.startDate) && (curJulDay <= p1Data.endDate)) {
						tStr += pPlanetStr[p1Data.id] + "=>"; 
					
						ComputeGetAntarDasa(2, p1Data.id, p1Data.dPrd, p1Data.startDate);
						for (int k=0; k<pCurAntarDasaData.size(); k++) {
							sDasaData p2Data = pCurAntarDasaData.get(k);
							if  ((curJulDay >= p2Data.startDate) && (curJulDay <= p2Data.endDate)) {
								tStr += pPlanetStr[p2Data.id];
								pStr.add(tStr);
								SweDate sd1 = new SweDate(p2Data.startDate);
								SweDate sd2 = new SweDate(p2Data.endDate);
								tStr = String.format("%4d-%02d-%02d", sd1.getYear(), sd1.getMonth(), sd1.getDay());
								pStr.add(tStr);
								tStr = String.format("%4d-%02d-%02d", sd2.getYear(), sd2.getMonth(), sd2.getDay());
								pStr.add(tStr);
								return pStr;
							}
						}
					}
				}
			}
		}
		return pStr;
	}
	
	
	public ArrayList<String> GetMahaDasaString() {
		ArrayList<String> pStr = new ArrayList<String>();
		pStr.clear();
		
		String tStr;
		
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
			SweDate sd1 = new SweDate(pData.startDate);
			SweDate sd2 = new SweDate(pData.endDate);
			
			tStr = String.format("%2s  %4d-%02d-%02d => %4d-%02d-%02d", pPlanetStr[pData.id], sd1.getYear(), sd1.getMonth(), sd1.getDay(),
					 sd2.getYear(), sd2.getMonth(), sd2.getDay());
			pStr.add(tStr);
		}
		
		return pStr;
	}

	
	public int[] GetDasaPlanetIds() {
		int[] tId = new int[pDasaData.size()];
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
			tId[i] = pData.id;
		}
		return tId;
	}
	
	public double[] GetDasaPeriods() {
		double[] tPrd = new double[pDasaData.size()];
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
			tPrd[i] = pData.dPrd;
		}
		return tPrd;
	}
	
	public double[] GetDasaStartDates() {
		double[] tstDate = new double[pDasaData.size()];
		for (int i=0; i<pDasaData.size(); i++) {
			sDasaData pData = pDasaData.get(i);
			tstDate[i] = pData.startDate;
		}
		return tstDate;
	}
	
	public void CalculateMahaDasa(double tMoon, double birthJulDay)
	{
		pMoonPos = tMoon;
		double temp = pMoonPos /totalPeriod;
		moonDasaLeft = 9.0 * (temp - (int)temp);      // Moon's longitude for dasha
		startId = (int)(moonDasaLeft);
		passedPeriod = (moonDasaLeft-startId) * (planetPrd[startId] * paramAyus/totalPeriod); // in Years
		dasaRemain = planetPrd[startId] - passedPeriod;
		nDasa  = 0;
		nLevel = nLevels;

		double startPeriod = birthJulDay - (passedPeriod * daysPerYear);
		double endAyus = birthJulDay + (totalAyus * daysPerYear);    // endAyus is end year till dasha reqd (120 Years)
	    
		int i1;
		int level = 0;
		pDasaData.clear();
		
		for(int i = startId; startPeriod < endAyus;i++)
		{
			i1 = (i % nDasas);
			
			double endPrd = startPeriod + ((planetPrd[i1] * paramAyus/totalPeriod)*daysPerYear);
			sDasaData pData = new sDasaData(level, pIdToDaySeq[i1], (planetPrd[i1]/totalPeriod), startPeriod,endPrd);
			startPeriod = endPrd;
			
			pDasaData.add(pData);
			nDasa++;
		}
	}
	
	
	public ArrayList<String> GetAntarDasaString() {
		ArrayList<String> pStr = new ArrayList<String>();
		pStr.clear();
		//ArrayList<sDasaData> pCurAntarDasaData = GetAntarDasa(iLevel, stId, curPrd, curStDate);
		
		String tStr;
		for (int i=0; i<pCurAntarDasaData.size(); i++) {
			sDasaData pData = pCurAntarDasaData.get(i);
			SweDate sd1 = new SweDate(pData.startDate);
			SweDate sd2 = new SweDate(pData.endDate);
			
			tStr = String.format("%2s  %4d-%02d-%02d => %4d-%02d-%02d", pPlanetStr[pData.id], sd1.getYear(), sd1.getMonth(), sd1.getDay(),
					 sd2.getYear(), sd2.getMonth(), sd2.getDay());
			pStr.add(tStr);
		}
		
		return pStr;
	}

	public int[] GetAntarDasaPlanetIds() {
		int[] tId = new int[pCurAntarDasaData.size()];
		for (int i=0; i<pCurAntarDasaData.size(); i++) {
			sDasaData pData = pCurAntarDasaData.get(i);
			tId[i] = pData.id;
		}
		return tId;
	}
	
	
	public double[] GetAntarDasaPeriods() {
		double[] tPrd = new double[pCurAntarDasaData.size()];
		for (int i=0; i<pCurAntarDasaData.size(); i++) {
			sDasaData pData = pCurAntarDasaData.get(i);
			tPrd[i] = pData.dPrd;
		}
		return tPrd;
	}
	
	public double[] GetAntarDasaStartDates() {
		double[] tstDate = new double[pCurAntarDasaData.size()];
		for (int i=0; i<pCurAntarDasaData.size(); i++) {
			sDasaData pData = pCurAntarDasaData.get(i);
			tstDate[i] = pData.startDate;
		}
		return tstDate;
	}
		
	// Calculate AntarDasa at all levels starting from AD (AD, PD, SD, PAD)
	public void ComputeGetAntarDasa(int level, int stId, double prd, double startDate)
	{
		double startPeriod = startDate;
	    //int    nDasa = 0;
		level = level + 1;

		pCurAntarDasaData = new ArrayList<sDasaData>();
		pCurAntarDasaData.clear();
		
		int i1;
	    stId = pIdToDasaSeq[stId] + dasaSeqStart;

		for(int i = stId; i< (stId + nDasas);i++)
		{
			i1 = (i % nDasas);

			double endPrd = startPeriod +((prd * planetPrd[i1] * paramAyus/totalPeriod)*daysPerYear);
			sDasaData pData = new sDasaData(level, pIdToDaySeq[i1], (prd * planetPrd[i1]/totalPeriod), startPeriod, endPrd);
			startPeriod = endPrd;
			pCurAntarDasaData.add(pData);
		}
	}
}
