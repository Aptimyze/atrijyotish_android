/*
** Module      : AtriJyotishCalc
** File:       : MatchCalc.cpp
** Description : Calculation for Marriage matching
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


// Document by Sarajit Poddar
// Principles of Marriage Matching
// Sambandha#2 by Pd. Sanjay Rath: 
//		http://srath.com/jyoti%E1%B9%A3a/amateur/sambandha
//		http://srath.com/jyoti%E1%B9%A3a/amateur/sambandha2

package org.bphs.atrijyotish_calc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//import android.content.Context;

public class MatchCalc {
	String [] grahaNames = {"Su", "Mo", "Ma", "Me", "Ju", "Ve", "Sa", "Ra", "Ke"};
    String [] rasiNames  = {"Ar", "Ta", "Ge", "Ca", "Le", "Vi", "Li", "Sc","Sg", "Cp", "Aq", "Pi"};
    int P_SU=0, P_MO=1, P_MA=2, P_ME=3, P_JU=4, P_VE=5, P_SA=6, P_RA=7, P_KE=8;

    String [] sRes = {"Bad", "Ok", "Good"};
	String [] fm = {"M", "F"};   // 0 is Male  and 1 is female
    String [] kuta1Names = {"Nadi/Pada Kuta", "Rasi Kuta", "Gana Kuta", "Graha Maitri",
    						"Yoni Kuta", "Dina Kuta", "Vasya Kuta", "Varna Kuta",};
    String [] kuta2Names = {"Mahendra Kuta", "Sthree Deerga", "Rajju Kuta", "Vedha Kuta", "Gotra", 
			"Vihanga", "Yonyanukula", "Bhuta Kuta", "Ashtakavarga", "Aya-Vyaya", 
			"Upapada", "Kuja Dosha"};
    String [] pStarNames = {
    		"Aswini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra", "Punarvasu", "Pushya", "Ashlesha", 
    		"Magha", "P.Phalguni","U.Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", 
    		"Mula", "P.Ashadha", "U.Ashadha", "Shravana", "Dhanishtha", "Shatabhisha", "P.Bhadrapada", "U.Bhadrapada", "Revati"
    		};

	Byte REL_AM=2, REL_M=1, REL_N=0, REL_S=-1, REL_AS=-2;
	ChartData mChart, fChart;
	String pPath;
	
    int [] tPoints = {8,7,6,5,4,3,2,1};
    int [] kPoints = {0,0,0,0,0,0,0,0};
    int [] kNoPoints = {0,0,0,0,0,0,0,0,0,0,  0,0,0,0};   // 0: No; 1= OK; 2 = Yes
    String [] sPoints = new String[15];
    int nKutaI = 1;
    public int nKutaP = 8;
    public int nKutaQ = 12;
	int nTotalPoints = 36;
	int nPoints = 0;
    
    public String testStr= "";
    
    public void SetDataPath(String tPath) {
    	pPath = tPath;
    }

    public void SetCharts(ChartData tDataMale, ChartData tDataFemale) {
    	mChart = tDataMale;
    	fChart = tDataFemale;
    	for(int i=0;i<nKutaQ;i++) sPoints[i] = "Excellant";
    	for(int i=0;i<nKutaP;i++) kPoints[i] = 0;
    }

   
    public ArrayList<String> GetKutaDetails(int curKuta) {
    	ArrayList<String> tList = new ArrayList<String>();
    	tList.clear();
    	if (curKuta < 0) curKuta = 0;
    	if (curKuta >= (nKutaI+nKutaP + nKutaQ)) curKuta = (nKutaI+nKutaP + nKutaQ-1);
    	int ii;

    	tList.add(String.format("%02d", curKuta));
		tList.add(String.format("%2d/%2d", (curKuta+1), (nKutaI+nKutaP + nKutaQ)));
    	
    	if (curKuta == 0)
    		tList.add(String.format("%2d/%2d : All Kutas", nPoints, nTotalPoints));
    	else if (curKuta <= nKutaP)
    		tList.add(String.format("%2d/%2d : %s", kPoints[curKuta-1], tPoints[curKuta-1], kuta1Names[curKuta-1]));
    	else {
    		ii = curKuta - nKutaP-1;
    		tList.add(String.format("%3s : %s", sRes[kNoPoints[ii]], kuta2Names[ii]));
    	} 

    	tList = LoadKutaDetails(curKuta, tList);
    	
    	return tList;
    }
    
    public ArrayList<String> GetKutaList() {
    	ArrayList<String> tList = new ArrayList<String>();
    	tList.clear();

    	// Caluclate and Add each kuta
    	tList.add(GetNadiKuta());
    	tList.add(GetRasiKuta());
    	tList.add(GetGanaKuta());
    	tList.add(GetGrahaMaitri());
    	tList.add(GetYoniKuta());
    	tList.add(GetDinaKuta());
    	tList.add(GetVasyaKuta());
    	tList.add(GetVarnaKuta());
    	
    	tList.add(GetMahendraKuta());
    	tList.add(GetSthreeKuta());
    	tList.add(GetRajjuKuta());
    	tList.add(GetVedha());
    	tList.add(GetGotraKuta());
    	tList.add(GetVihangaKuta());
    	tList.add(GetYonyanukula());
    	tList.add(GetBhutaKuta());
    	//tList.add(GetVayanukulya());  // Not clear. Not required
    	tList.add(GetAshtakavarga());
    	//tList.add(GetChittanukulya());   // Not clear. Not required
    	tList.add(GetAyaVyaya());
    	//tList.add(GetRnaDhana());  //TODO
    	
    	tList.add(GetUpapada());
    	tList.add(GetKujaDosha());

    	// Insert total points at 1st
    	nPoints = 0;
    	for (int i=0;i< nKutaP;i++) {
    		nPoints += kPoints[i];
    	}
    	
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);

		String tStr = String.format("%2d/%2d %-6.6s: %1d(M); %-6.6s: %1d(F)", 
    			nPoints, nTotalPoints, pStarNames[mStar[0]] , (mStar[1]+1), pStarNames[fStar[0]], (fStar[1]+1));
    	tList.add(0, tStr);
    	
    	return tList;
    }

	public String GetLagnaMatch() {
		String tStr;
		int mRasi = mChart.GetLagnaSign();
		int fRasi = fChart.GetLagnaSign();
		tStr = "Lagna: " + rasiNames[fRasi] + "(F); ";
		tStr += rasiNames[mRasi] + "(M) => ";
		
		// Count from Female to Male
		if (mRasi < fRasi) mRasi += 12;
		int fCount = mRasi - fRasi + 1;
		int mCount = 14 - fCount;
		
		tStr += String.format("Relation (F->M): (%d-%d)", fCount, mCount); 
		
		return tStr;
	}
	

	// Nadi/Nakshatra Pada Kuta: First find Nadi, if not find Nakshatra pada
	public String GetNadiKuta()
	{
		String [] nadiNames = {"Vata", "Pitta", "Kapha"};
		byte [] nadiType = {0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0, 0,1,2,2,1,0,0,1,2};
		byte [] nadiPadaType = {0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0, 0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0,
							   0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0, 0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0,
							   0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0, 0,1,2,2,1,0,0,1,2, 2,1,0,0,1,2,2,1,0,
							   };
		String tStr = "";
		kPoints[0] = 0;
		int iRes = 1; // 0 (No); 1(OK); 2(Good)
	
		// Get Moon Nakshatras
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);

		// Find naadi Kuta using Moon Star
		if (nadiType[mStar[0]] != nadiType[fStar[0]]) { kPoints[0] = tPoints[0]; iRes = 2;}  // Not the same

		if (nadiType[mStar[0]] == nadiType[fStar[0]]) {    // When same
			if (nadiType[mStar[0]] == 1) iRes = 0;         // Middle is not good, others OK
		} 

		// TODO: Is this required?  Find naadi Kuta using Moon Nakshatra Pada
		int iRes1 = 0; // 0 (No); 1(OK); 2(Good)
		int ind1 = mStar[0]*4 + mStar[1];
		int ind2 = fStar[0]*4 + fStar[1];

/* TODO: Is this required?
 		if (kPoints[0] == 0) {
 			if (nadiPadaType[ind1] != nadiPadaType[ind2]) { kPoints[0] = tPoints[0]; iRes1 = 2;}  // Not the same
		}
*/
		
		tStr = String.format("%1d/%1d %s\n    Star:%s(F),%s(M):%s\n    Pada:%s(F),%s(M):%s", 
				kPoints[0], tPoints[0], kuta1Names[0], 
				nadiNames[nadiType[fStar[0]]], nadiNames[nadiType[mStar[0]]], sRes[iRes], 
				nadiNames[nadiPadaType[ind2]], nadiNames[nadiPadaType[ind1]], sRes[iRes1] );
		
		return tStr;
	}

	// Compare Moon sign: Count From female to male
	public String GetRasiKuta()
	{
		String tStr = "";
		kPoints[1] = 0;
		int mMoonsign = mChart.GetPlanetRasi(P_MO);
		int fMoonsign = fChart.GetPlanetRasi(P_MO);
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		
		int count = mMoonsign - fMoonsign + 1;
		if (count <= 0) count += 12;
		String eStr = "";
		
		if ((count > 1) && (count < 7)) {   // Invisible half
			kPoints[1] = 0;  eStr = "Invisbile half";	
		} else if ((count == 8) || (count == 12)) {   // Disthana
			kPoints[1] = 0;	 eStr = "Dusthana";
		} else if (count == 1) {   // Same Raasi
			if (mStar[0] == fStar[0])  { kPoints[1] = 0; eStr = "Same Rasi+Star"; }   // Same Star is bad
			else kPoints[1] = tPoints[1];               // Different Star
		} else kPoints[1] = tPoints[1];   // 7,9,10,11 are good
	
		
		tStr = String.format("%1d/%1d %s\n    %2s to %2s:%d", 
				kPoints[1], tPoints[1], kuta1Names[1], rasiNames[fMoonsign], rasiNames[mMoonsign], count);
		if (eStr.length() > 0) tStr += "(" + eStr + ")";
		
		return tStr;
	}

	// based on Moon Star: Deva, nara, Rakshasa
	// TODO/Verify: This also has to see from Moon star; Lagna Star; Chandramsa (D-9 Moon star)??
	public String GetGanaKuta()
	{
		String tStr = "";
		String eStr = "";
		
		kPoints[2] = 0;
		byte [] ganaType = {0,1,2,1,0,1,0,0,2,  2,1,1,0,2,0,2,0,2,  2,1,1,0,2,2,1,1,0};
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);

		if (ganaType[mStar[0]] == ganaType[fStar[0]]) { 
			kPoints[2] = tPoints[2]; eStr = "Favourable";
		} else if ((ganaType[mStar[0]] == 0) && (ganaType[fStar[0]] == 1)) {
			kPoints[2] = tPoints[2]; eStr = "Fair";
		} else if ((ganaType[mStar[0]] == 2) && (ganaType[fStar[0]] == 1)) {
			kPoints[2] = tPoints[2]; eStr = "OK";
		} else {
			kPoints[2] = 0; eStr = "Very bad";
		}
		
		// TODO: If sthree deergam, no need to see this
		
		String [] ganaNames = {"Deva", "Nara", "Rakshasa"};
		
		tStr = String.format("%1d/%1d %s (%s)\n    %s(F); %s(M)", 
				kPoints[2], tPoints[2], kuta1Names[2], eStr,
				ganaNames[ganaType[fStar[0]]], ganaNames[ganaType[mStar[0]]]);
		return tStr;
	}

	// AtiMitra, Mitra, Sama, Satru, AtiSatru
	// Only lord with body is used (Ra, ke not used)

	/*
	When Lords of the Moonsigns are Friends. (5 pts)
	- When Lord (A) is friend to Lord (B) & Lord (B) is neutral to Lord (A) this is good. (4 pts).
	- When both are neutral Graha Maitram is mediocre. (3 pts)
	- When one is friendly and the other is inimical. (1 pt)
	- When one is neutal and the other is inimical. (1/2 pt).
	- When both are enemies Graha Maitram is (0)
	- TODO: However if a friendship exists between the Lords of the Moonsigns in the Navamsa 3 pts are given instead of zero, 1/2 or 1 pt.
*/
	
	public String GetGrahaMaitri()
	{
		String [] pRelNames = {"Ati.S", "Satru", "Sama", "Mitra", "Ati.M"};
		String tStr = "";
		kPoints[3] = 0;

		// Subhapati
		int mLord = mChart.GetSignLordOfPlanet1(P_MO);  // Get Rasi lord (Moon sign): Subhapati
		int fLord = fChart.GetSignLordOfPlanet1(P_MO);  

		// TODO: Do we use Natural relation OR Compund relation??
		//int fRel = fChart.GetCompRelation(fLord, mLord);   // Relation of female LL with male LL
		//int mRel = mChart.GetCompRelation(mLord, fLord);   // Relation of male LL with female LL
		int fRel = fChart.GetNatRelation(fLord, mLord);   // Relation of female LL with male LL
		int mRel = mChart.GetNatRelation(mLord, fLord);   // Relation of male LL with female LL

		// Only Female to male relation is seen: TODO: Is M to F required?
		// TODO: If > Sama points counted? Is this correct? or better proportianate
		//if (fRel >= 0) kPoints[3] = tPoints[3];

		if ((fRel == REL_M) && (mRel == REL_M)) kPoints[3] = tPoints[3];
		if ((fRel == REL_M) && (mRel == REL_N)) kPoints[3] = tPoints[3]-1;
		if ((fRel == REL_M) && (mRel == REL_S)) kPoints[3] = tPoints[3]-3;
		if ((fRel == REL_N) && (mRel == REL_M)) kPoints[3] = tPoints[3]-1;
		if ((fRel == REL_N) && (mRel == REL_N)) kPoints[3] = tPoints[3]-2;
		if ((fRel == REL_N) && (mRel == REL_S)) kPoints[3] = tPoints[3]-4;
		if ((fRel == REL_S) && (mRel == REL_M)) kPoints[3] = tPoints[3]-4;
		if ((fRel == REL_S) && (mRel == REL_N)) kPoints[3] = tPoints[3]-4;
		if ((fRel == REL_S) && (mRel == REL_S)) kPoints[3] = tPoints[3]-5;
														
		tStr = String.format("%1d/%1d %s\n    (%2s,%2s:%s);(%2s,%2s:%s)", kPoints[3], tPoints[3], kuta1Names[3],
				grahaNames[fLord], grahaNames[mLord], pRelNames[fRel+2],
				grahaNames[mLord], grahaNames[fLord], pRelNames[mRel+2]);
		return tStr;
	}

	public String GetYoniKuta()
	{
		String [] animalNames = {"Hors","Elep","Goat", "Serp", "Dog", 
				                 "Cat", "Rat", "Cow", "Buff", "Tiger", "Hare",
				                 "Monk", "Mong", "Lion"};
		byte [] yoniType  = {0,0,1,0,1,1,1,0,0,  0,1,0,1,1,0,0,1,0,  0,0,0,1,1,1,0,1,1};
		byte [] animalType = {0,1,2,3,3,4,5,2,5,  6,6,7,8,9,8,9,10,10, 4,11,13,11,12,0,12,7,1};
		byte [] score = {4,2,2,3,2,2,2,1,0,1,1,3,2,1,  2,4,5,5,2,2,2,2,3,1,2,3,2,0,
						 2,3,4,2,1,2,1,3,3,1,2,0,3,1,  3,3,2,4,2,1,1,1,1,2,2,2,0,2,
						 2,2,1,2,4,2,1,2,2,1,0,2,1,1,  2,2,2,1,2,4,0,2,2,1,3,3,2,1,
						 2,2,1,1,1,0,4,2,2,2,2,2,1,2,  1,2,3,1,2,2,2,4,3,0,3,2,2,1,
						 0,3,3,1,2,2,2,3,4,1,2,2,2,2,  1,1,1,2,1,1,2,0,1,4,1,1,2,1,
						 1,2,2,2,0,3,2,3,2,1,4,2,2,1,  3,3,0,2,2,3,2,2,2,1,2,4,3,2,
						 2,2,3,0,1,2,1,2,2,2,2,3,4,2,  1,0,1,2,1,1,2,1,2,1,1,2,2,4};
		
		String tStr = "";
		String eStr = "";
		kPoints[4] = 0;
		
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		
		// Male female based compatability
		if ((yoniType[fStar[0]] == 1) && (yoniType[mStar[0]] == 0)) {  //Female in female star; Male in male star
			eStr = "Fin.stable"; 
		}else if ((yoniType[fStar[0]] == 1) && (yoniType[mStar[0]] == 1)) { // Both in Female star
			eStr = "Fin.Loss";
		} else if ((yoniType[fStar[0]] == 0) && (yoniType[mStar[0]] == 0)) { // Both in male star
			eStr = "Rejected";
		} else eStr = "Rejected";    // Female in male star and male in female star
		
		// Animal based points
		int ind = animalType[fStar[0]] * 14 + animalType[mStar[0]];
		kPoints[4] = score[ind]; 
				
		// TODO: Rasi yoni compatability
		String [] rasiAnimals = {"Pakshi", "Reptile", "Pasu", "Nara"};
		byte [] rasiAnimType = {2,2,3,1, 2,3,3,1, 3,0,3,0};
		//byte [] rasiYoni = {};  // TODO: Incomplete and also find compatibility

		int mMoonsign = mChart.GetPlanetRasi(P_MO);
		int fMoonsign = fChart.GetPlanetRasi(P_MO);
				
		tStr = String.format("%1d/%1d %s\n    (%s,%s);(%s,%s:%s)\n    Rasi Yoni:%s, %s", 
				kPoints[4], tPoints[4], kuta1Names[4],
				animalNames[animalType[fStar[0]]], animalNames[animalType[fStar[0]]], 
				fm[yoniType[fStar[0]]], fm[yoniType[mStar[0]]], eStr,
				rasiAnimals[rasiAnimType[fMoonsign]], rasiAnimals[rasiAnimType[mMoonsign]]);
		return tStr;
	}

	public String GetDinaKuta()
	{
		String tStr = "";
		kPoints[5] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		String eStr = "";
		
		int ind = mStar[0] - fStar[0] + 1;
		if (ind <= 0) ind += 27;
		int ind1 = (ind % 9);
		if (ind >= 9) ind1 = ind1 + 1;
		if ((ind1 == 3) || (ind1 == 5) || (ind1 == 7)) {
			eStr = "Troubles";
		} else if (ind1 == 6) {
			eStr = "Inauspicious";
		} else {
			eStr ="Good";
			kPoints[5] = tPoints[5];
		}
		
/* TODO To verify and add		
		String e2Star = "";
		int mPada = mStar[0]*4 + mStar[1];
		int fPada = fStar[0]*4 + fStar[1];
		int dPada = mPada - fPada + 1;
		if (dPada < 108) dPada = dPada + 108;
		dPada++;
		if ((dPada == 88) || (dPada == 108)) {
			e2Star = "Inauspicious"; 
			kPoints[5] = 0;
		}
*/
		// Do it also for Lagna Star
		// If Moon star Good AND Lagna Star Good ==> very good
		// If Moon star Good AND Lagna Star bad  ==> OK (Difference in major issues)
		// If Moon star Bad AND Lagna Star Good/Bad ==> Reject
		
		int [] mStarLagna = mChart.GetLagnaStar();
		int [] fStarLagna = fChart.GetLagnaStar();

		String lStr = "";
		//int lPts = 0; TODO
		
		int indL = mStarLagna[0] - fStarLagna[0] + 1;
		if (indL <= 0) indL += 27;
		int indL1 = (indL % 9);
		if (indL >= 9) indL1 = indL1 + 1;
		if ((indL1 == 3) || (indL1 == 5) || (indL1 == 7)) {
			lStr = "Troubles";
		} else if (indL1 == 6) {
			lStr = "InAuspicious";
		} else {
			lStr ="Good";
			//lPts = tPoints[5];
		}

		String [] taraNames = {"Janma", "Sampatha", "Vipatha", "Kshema", "Pratyaka", "Sadhana", "Naidhana", "Mitra", "Paramitra"};
				
		tStr = String.format("%1d/%1d %s\n    Mo*:%s:%s\n    Lg*:%s:%s", 
				kPoints[5], tPoints[5], kuta1Names[5], taraNames[ind1-1], eStr, taraNames[indL1-1], lStr);
		return tStr;
	}

	public String GetVasyaKuta()
	{
		String tStr = "";
		kPoints[6] = 0;
		byte [] vasya1 = {4,3, 5, 7,   6,  2,5, 3,  11,  0, 0, 9};
		byte [] vasya2 = {7,6,-1, 8,  -1, 11,9,-1,  -1, 10,-1,-1};
		
		int mMoonsign = mChart.GetPlanetRasi(P_MO);
		int fMoonsign = fChart.GetPlanetRasi(P_MO);

		String fRes="No", mRes="No";
		
		if ((vasya1[fMoonsign] ==  mMoonsign) || (vasya2[fMoonsign] ==  mMoonsign)) {
			kPoints[6] = tPoints[6]/2;
			fRes = "Yes";
		}
		
		if ((vasya1[mMoonsign] ==  fMoonsign) || (vasya2[mMoonsign] ==  fMoonsign)) {
			kPoints[6] += tPoints[6]/2;
			mRes = "Yes";
		}
		
		tStr = String.format("%1d/%1d %s\n    F(%2s)->M(%2s):%s\n    M(%2s)->F(%2s):%s", 
				kPoints[6], tPoints[6], kuta1Names[6],
				rasiNames[fMoonsign], rasiNames[mMoonsign], fRes, rasiNames[mMoonsign], rasiNames[fMoonsign], mRes);
		return tStr;
	}

/*
	In the Northern Indian system the Rasi’s are categorized into 
	four Varna (Brahmin, Kshatriya, Vaishya and Shudra). 
	The Kataka, Vrichika and Meenam rasi fall into the category of Brahmin Varna. 
	The  Mesha, Simham and Dhanur rasi fall into the category of Kshatriya  Varna. 
	The Rishaba, Kanya and Makara rasi fall into the category of Vaishya Varna. 
	The Mithuna, Khumba and Tula rasi fall into the category of Shudra Varna.
	
	If Woman in higher category as the Man. (0 pts)
	If Man is in a higher category than the woman. (1 pt)
	If they both belong to the same category. (1 pt).
 */
	public String GetVarnaKuta()
	{
		String tStr = "";
		kPoints[7] = 0;
		String [] varnaNames = {"Brahmin", "Kshatriya", "Vaisya", "Sudra"};
		int [] varna1 = {1,2,3,0,1,2,3,0,1,2,3,0}; 
		
		int mMoonsign = mChart.GetPlanetRasi(P_MO);
		int fMoonsign = fChart.GetPlanetRasi(P_MO);

		String fRes;
		
		if (varna1[fMoonsign] == varna1[mMoonsign]) {
			kPoints[7] = tPoints[7];
			fRes = "Best";
		} else if (varna1[mMoonsign] < varna1[fMoonsign]) {  // Man's caste is higher
			kPoints[7] = tPoints[7];
			fRes = "OK";
		} else {
			kPoints[7] = 0;
			fRes = "No";
		}
		
		tStr = String.format("%1d/%1d %s\n    %s,%s: %s", kPoints[7], tPoints[7], kuta1Names[7],
				varnaNames[varna1[fMoonsign]], varnaNames[varna1[mMoonsign]], fRes);
		return tStr;
	}

	
/*  Varna kuta based on Nakshatras: TODO Verify is this correct or based on rasi is correct?
 *  In the South Indian System, the 27 nakshatra is divided into 4 varnas. 
 *  Ashwini, Punarvasu, Hasta, Moola, Purva-Bhadrapada are categorized as Brahmanas (learned) Varna; 
 *  Bharani, Pushya, Chitra, PurvaAsadha, Uttara-Bhadrapada are categorized as Kshatriyas (warriors) Varna;
 *   Krittika, Aslesha, Swati, UttaraAsadha, Revati are categorized as Vaisyas (traders) Varna; 
 *   Rohini, Magha, Vishakha, Sravana are categorized as Sudra (worker) Varna. 
 *   But checking varna is not followed nowadays. 
 *   According to the South Indian astrologers it is excellent for the boys and girls nakshatra falls in the same Varna
 * 	
	public String GetVarnaKuta()
	{
		String tStr = "";
		kPoints[7] = 0;
		//String [] varnaNames = {"Brahmin", "Kshatriya", "Vaisya", "Sudra", "Anuloma", "Pratiloma"};
		//int [] varna1 = {0,1,2,3,4,5, 0,1,2,3,4,5, 0,1,2,3,4,5, 0,1,2,3,4,5, 0,1,2}; 
		String [] varnaNames = {"Brahmin", "Kshatriya", "Vaisya", "Sudra"};
		int [] varna1 = {0,1,2,3,4,5, 0,1,2,3,4,5, 0,1,2,3,4,5, 0,1,2,3,4,5, 0,1,2}; 
		
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		String fRes;
		
		if (varna1[fStar[0]] == varna1[mStar[0]]) {
			kPoints[7] = tPoints[7];
			fRes = "Best";
		} else if ((varna1[mStar[0]] == 5) || (varna1[fStar[0]] == 5)) {
			kPoints[7] = 0;
			fRes = "No";
		} else if ((varna1[mStar[0]] == 4) && (varna1[fStar[0]] <4)) {
			kPoints[7] = tPoints[7];
			fRes = "OK";
		} else if ((varna1[fStar[0]] == 4) && (varna1[mStar[0]] <4)) {
			kPoints[7] = tPoints[7];
			fRes = "OK";
		} else if (varna1[mStar[0]] < varna1[fStar[0]]) {  // Man's caste is higher
			kPoints[7] = tPoints[7];
			fRes = "OK";
		} else {
			kPoints[7] = 0;
			fRes = "No";
		}
		
		tStr = String.format("%1d/%1d %s\n    %s,%s: %s", kPoints[7], tPoints[7], kuta1Names[7],
				varnaNames[varna1[fStar[0]]], varnaNames[varna1[mStar[0]]], fRes);
		return tStr;
	}
*/
	

	// Kuta without points  --------------------------------------------------------------------------------

	public String GetMahendraKuta()
	{
		String tStr = "";
		kNoPoints[0] = 0;
		
		Byte [] mKuta = {4, 7, 10, 13, 16, 19, 22, 25};
		
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		
		int cnt = mStar[0] - fStar[0] + 1;
		if (cnt <= 0) cnt += 27;
				
		for (int i=0; i < mKuta.length;i++) {
			if (cnt == mKuta[i]) {
				kNoPoints[0] = 2;
				break;
			}
		}
		
		tStr = String.format("%4s %d: %s", sRes[kNoPoints[0]], cnt, kuta2Names[0]);
		return tStr;
	}

	public String GetSthreeKuta()
	{
		String tStr = "";
		kNoPoints[1] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		
		int cnt = mStar[0] - fStar[0] + 1;
		if (cnt <= 0) cnt += 27;
		
		if (cnt <= 9)       kNoPoints[1] = 0;
		else if (cnt <= 18) kNoPoints[1] = 1;
		else 			    kNoPoints[1] = 2;
		
		tStr = String.format("%4s %d: %s", sRes[kNoPoints[1]], cnt, kuta2Names[1]);
		return tStr;
	}

	/*
	 0 Shiro (Head)   rajju: Dhanishta, Chitta and Mrigasira.
	 1 Kanta (Throat) rajju: Rohini, Ardra, Hasta, Swati. Sravana, and Satabhisha;
	 2 Kati (Waist)   rajju: Bharani, Pushyami, Pubba, Anuradha, Poorvashadha and Uttarabhadra;
	 3 Udara (Navel)  rajju:  Krittika, Punarvasu, Uttara, Visakha, Uttarashadha and Poorvabhadra;
	 4 Pada (Feet)    rajju: Aswini, Aslesha, Makha, Jyeshta, Moola and Revati;
	 */
	public String GetRajjuKuta()
	{
		String tStr = "";
		kNoPoints[2] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		Byte [] rKuta = {4, 2, 3, 1, 0, 1, 3,2,4,  4,2,3,1,0,1,3,2,4,  4,2,3,1,0,1,3,2,4};
		
		if (rKuta[fStar[0]] != rKuta[mStar[0]]) kNoPoints[2] = 2;     // Not same
		
		String [] rKutaNames = {"Siro","Kanta", "Kati", "Udara","Pada"};
		
		tStr = String.format("%4s (%s:%s): %s", sRes[kNoPoints[2]], 
				 rKutaNames[rKuta[fStar[0]]], rKutaNames[rKuta[mStar[0]]], kuta2Names[2]);
		return tStr;
	}
/*
	Aswini and Jyeshta;
	Bharani and Anuradha;
	Krittika and Visakha;
	Rohini and Swati;
	Aridra and Sravana
	Punarvasu and Uttarashadha;
	Pusayami and Purvashadha;
	Aslesha and Moola;
	Makha and Revati;
	Purvaphalguni and Uttarabhadrapada;
	Uttaraphalguni and Purvabhadrapada;
	Hasta and Satabhisha,
	Mrigasira and Dhanishta.
  
  TODO: What is for Chitta??
 */
	public String GetVedha()
	{
		String tStr = "";
		kNoPoints[3] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		Byte [] vKuta = {17,16, 15,14, 22, 21,20,19,18, 26, 25,24,23,13,  3,2,1,0, 8,7,6,5, 4, 12,11,10,9};
		
		if ((fStar[0] == vKuta[mStar[0]]) || (mStar[0] == vKuta[fStar[0]]))
			kNoPoints[3] = 0;
		else
			kNoPoints[3] = 2;
		
		tStr = String.format("%4s %s", sRes[kNoPoints[3]], kuta2Names[3]);
		return tStr;
	}

	/*
	0: Aswini, Pushya, Swati : Maricha gotra
	1: Bharani, Aslesha, Visakha and Shravana: Vasisthta gotra
	2: Kritika, Magha, Anuradha and Dhanista: Angirasa gotra
	3: Rohini, Purvaphalguni, Jyesta and Shatabhisha: Atri gotra
	4: Mrigashira, Uttaraphalguni, Moola and Purvabhadra: Pulastya gotra
	5: Aridra, Hasta, Purvashadha and Uttarabhadra: Pulaha gotra
	6: Punarvasu, Chitra, Uttarashadha and Revati: Kritu gotra 
	 */
	
	public String GetGotraKuta()
	{
		String tStr = "";
		kNoPoints[4] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		Byte [] gKuta = {0,1,2,3,4,5,6, 0,1,2,3,4,5,6, 0,1,2,3,4,5,6, 1,2,3,4,5,6};
		
		if (gKuta[fStar[0]] == gKuta[mStar[0]]) kNoPoints[4] = 0;
		else                                    kNoPoints[4] = 2;
		
		String [] rishi = {"Maricha", "Vasista", "Angiras", "Atri", "Pulasty", "Pulaha", "Kritu"};
				
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[4]], 
				rishi[gKuta[fStar[0]]], rishi[gKuta[mStar[0]]] , kuta2Names[4]);
		return tStr;
	}

/*
	Bharandhaka (Ashwini, Bharani, Krittika, Rohini, Mrigasira)
	Pingala (Arudra,Punarvasu, Pushyami, Aslesha, Magha, P.Phalguni), 
	Crow (U.Phalguni, Hasta, Chitra, Swati, Visakha, Anuradha)
	Cock (Jyestha, Moola, P.Ashadha, U.Ashadha, Shravana)
	Peacock (Dhanishta, Satabhistha, P.Bhadra, U.Bhadra, Revati)
*/
	public String GetVihangaKuta()
	{
		String tStr = "";
		kNoPoints[5] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		Byte [] vKuta = {0,0,0,0,0, 1,1,1,1,1,1, 2,2,2,2,2,2, 3,3,3,3,3, 4,4,4,4,4};
		
		if (vKuta[fStar[0]] == vKuta[mStar[0]]) kNoPoints[5] = 2;
		
		String [] Pakshi = {"Garuda", "Pingala", "Crow", "Cock", "Peacock"};
		
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[5]], 
				Pakshi[vKuta[fStar[0]]],  Pakshi[vKuta[mStar[0]]], kuta2Names[5]);
		return tStr;
	}

	public String GetYonyanukula()
	{
		String tStr = "";
		kNoPoints[6] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		byte [] yoniType  = {0,0,1,0,1,1,1,0,0,  0,1,0,1,1,0,0,1,0,  0,0,0,1,1,1,0,1,1};

		// Male female based compatability
		if ((yoniType[fStar[0]] == 1) && (yoniType[mStar[0]] == 0)) {  //Female in female star; Male in male star
			kNoPoints[6] = 2; 
		}else if ((yoniType[fStar[0]] == 1) && (yoniType[mStar[0]] == 1)) { // Both in Female star
			kNoPoints[6] = 1;
		} else kNoPoints[6] = 0;   // Both in male star
		
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[6]], 
				fm[yoniType[fStar[0]]], fm[yoniType[mStar[0]]], kuta2Names[6]);
		return tStr;
	}
	
	/*
	 Aswini to Mrigashira: Bhoohu (earth)
	 Arudhra to Purvaphalgui: Apaha (water)
	 Uttaraphalguni to Visakha : Agni (fire)
	 Anuradha to Sravana : Vayu (air)
	 Dhanista to Revati: Akasha (Ether)
	 */
	
	public String GetBhutaKuta()
	{
		String tStr = "";
		kNoPoints[7] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);
		Byte [] bKuta = {0,0,0,0,0, 1,1,1,1,1,1, 2,2,2,2,2, 3,3,3,3,3,3, 4,4,4,4,4};

		if (bKuta[fStar[0]] == bKuta[mStar[0]]) kNoPoints[7] = 2;
		if ((bKuta[fStar[0]] == 2) && (bKuta[mStar[0]] == 3)) kNoPoints[7] = 2;
		if ((bKuta[fStar[0]] == 3) && (bKuta[mStar[0]] == 2)) kNoPoints[7] = 2;
		if ((bKuta[fStar[0]] == 0) || (bKuta[mStar[0]] == 0)) kNoPoints[7] = 2;
		// TODO: What happens: (A) Water+Air) (B) Combination with Akash?
		// TODO: Is it required to do also using Moon sign?
		
		String [] bhuta = {"Bhoo", "Jala", "Agni", "Vayu", "Ether"};
		
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[7]], 
				bhuta[bKuta[fStar[0]]], bhuta[bKuta[mStar[0]]], kuta2Names[7]);
		return tStr;
	}

	// M:(F:3, M:6):Good/Not Good ==> M: W.r.o Moon rasi in male chart; F:3 In the female chart moon Bindus)
	//   M:6 : In the male chart Moon bindus
	public String GetAshtakavarga()
	{
		String tStr = "";
		kNoPoints[8] = 0;

		byte [] mRasiId = mChart.GetAllPlanetsRasi();
		mRasiId[P_SA+1] = mChart.GetLagnaRasi();         // Place Lagna rasi ID in place of Ra (As Ra, Ke not used)
		
		byte [] fRasiId = fChart.GetAllPlanetsRasi();
		fRasiId[P_SA+1] = fChart.GetLagnaRasi();         // Place Lagna rasi ID in place of Ra (As Ra, Ke not used)
		
		byte mmBindu = GetBindus(mRasiId[P_MO], mRasiId);
		byte mfBindu = GetBindus(mRasiId[P_MO], fRasiId);
		
		byte ffBindu = GetBindus(fRasiId[P_MO], fRasiId);
		byte fmBindu = GetBindus(fRasiId[P_MO], mRasiId);
		
		if ((mfBindu > mmBindu) && (fmBindu > ffBindu)) kNoPoints[8] = 2;

		String mStr = "Not good";
		if (mfBindu > mmBindu) mStr = "Good";
		
		String fStr = "not good";
		if (fmBindu > ffBindu) fStr = "Good";
		
		// TODO: Also calculate SAV of male Moon sign in femlae chart and female moon sign in male chart
		
		tStr = String.format("%4s %s\n     M:(F:%d, M:%d):%s\n     F:(F:%d, M:%d):%s", sRes[kNoPoints[8]], kuta2Names[8],
				mfBindu, mmBindu, mStr, ffBindu, fmBindu, fStr);
		return tStr;
	}

	private byte GetBindus(byte moonSign, byte [] rId) {
		byte [][] bindusMoon = {
				//1  2  3  4  5  6  7  8  9 10 11 12
				{ 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0 },  // Sun
				{ 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0 },  // Moon
				{ 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0 },  // Mars
				{ 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0 },  // Mercury
				{ 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0 },  // Jupiter
				{ 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0 },  // Venus
				{ 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0 },  // Saturn
				{ 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0 }   // Lagna
			};
	
		byte nBindu = 0;
		for (int pId=0;pId<mChart.gdnPlanets-1;pId++) {  // 7 Planets + Lagna  (W.o Ra, Ke
			byte dist = (byte)(moonSign - rId[pId] + 1);
			if (dist <= 0) dist += 12;
			nBindu += bindusMoon[pId][dist-1];
		}
		
		return nBindu;
	}
	
	public String GetAyaVyaya()
	{
		String tStr = "";
		kNoPoints[9] = 0;
		int [] mStar = mChart.GetPlanetStar(P_MO);
		int [] fStar = fChart.GetPlanetStar(P_MO);

		int vyaya = mStar[0] - fStar[0] + 1;   // Vyaya
		if (vyaya <= 0) vyaya += 27;
		vyaya = vyaya * 5;
		vyaya = (vyaya % 7);

		int aya = fStar[0] - mStar[0] + 1;   // Aya
		if (aya <= 0) aya += 27;
		aya = aya * 5;
		aya = (aya % 7);
		
		String aStr = "Gain";
		if (vyaya > aya) aStr = "Loss";
		
		tStr = String.format("%4s (Aya:%d, vyaya:%d):%s", aStr, aya, vyaya, kuta2Names[9]);
		return tStr;
	}	

	public String GetUpapada()
	{
		String tStr = "";
		kNoPoints[10] = 0;
		//int [] mStar = mChart.GetPlanetStar(P_MO);
		//int [] fStar = fChart.GetPlanetStar(P_MO);
		//Byte [] vKuta = {0,0,0,0,0, 1,1,1,1,1,1, 2,2,2,2,2,2, 3,3,3,3,3, 4,4,4,4,4};
		
		byte [] fArudha = fChart.GetArudha();
		byte [] mArudha = mChart.GetArudha();
		int mLagnaRasi = (mChart.GetLagnaSign() + 11)%12;
		int fLagnaRasi = (fChart.GetLagnaSign() + 11)%12;

		String fStr1 = rasiNames[fArudha[11]];
		String fStr2 = rasiNames[(fArudha[11]+1)%12];
		
		if ((fLagnaRasi == 7) || (fLagnaRasi == 10)) { // If Sc or AQ (2nd Arudha 
				fStr1 += rasiNames[fArudha[13]];
				fStr2 += rasiNames[(fArudha[13]+1)%12];
		}

		String mStr1 = rasiNames[mArudha[11]];
		String mStr2 = rasiNames[(mArudha[11]+1)%12];
		
		if ((mLagnaRasi == 7) || (mLagnaRasi == 10)) { // If Sc or AQ (2nd Arudha 
				mStr1 += "," + rasiNames[mArudha[13]];
				mStr2 += "," + rasiNames[(mArudha[13]+1)%12];
		}
		
		tStr = String.format("%4s %s\n     F:UL(%s); UL2(%s)\n     M:UL(%s); UL2(%s)", "    ", kuta2Names[10],
				fStr1, fStr2, mStr1, mStr2);
		return tStr;
	}	
	
	public String GetKujaDosha()
	{
		String tStr = "";
		kNoPoints[11] = 0;
		//int [] mStar = mChart.GetPlanetStar(P_MO);
		//int [] fStar = fChart.GetPlanetStar(P_MO);
		Byte [] kujaH = {0,1,1,0,1,0,0, 1,1,0,0,0,1};  // COunt so indexed from 1 (0 not used)
		
		// 
		int mLagnaRasi = mChart.GetLagnaSign();
		int fLagnaRasi = fChart.GetLagnaSign();

		int mMoonRasi = mChart.GetPlanetRasi(P_MO);
		int fMoonRasi = fChart.GetPlanetRasi(P_MO);

		int mMarsRasi = mChart.GetPlanetRasi(P_MA);
		int fMarsRasi = fChart.GetPlanetRasi(P_MA);
		
		// Step 1: Find if 1,2,4,7,8,12 From Lagna, Moon (TODO: Venus/Jupiter (Male/female)
		int mCntLagna = mMarsRasi - mLagnaRasi + 1;
		if (mCntLagna <=0) mCntLagna += 12;
		
		int mCntMoon = mMarsRasi - mMoonRasi + 1;
		if (mCntMoon <=0) mCntMoon += 12;
		
		int fCntLagna = fMarsRasi - fLagnaRasi + 1;
		if (fCntLagna <=0) fCntLagna += 12;
		
		int fCntMoon = fMarsRasi - fMoonRasi + 1;
		if (fCntMoon <=0) fCntMoon += 12;

		int mDosha = 0;
		int fDosha = 0;

		String fStr = "None";

		if (kujaH[fCntLagna] == 1) {
			fStr = "From Lg";   fDosha = 1;
			if (kujaH[fCntMoon] == 1) {
				fStr += "; Mo (Confirm)";
			}
		} else if (kujaH[fCntMoon] == 1) {
			fStr = "From Mo";  fDosha = 1;
		}
	
		String mStr = "None";
		if (kujaH[mCntLagna] == 1) {
			mStr = "From Lg";  mDosha = 1;
			if (kujaH[mCntMoon] == 1) {
				mStr += "; Mo (Confirm)"; 
			}
		} else if (kujaH[mCntMoon] == 1) {
			mStr = "From Mo";   mDosha = 1;
		}
		
		String eDosha = "None";
		if (mDosha != fDosha) eDosha = "Yes"; 
		//Step 2: TODO: Based on Conjoined planets
		
		
		tStr = String.format("%4s %s\n     F: Lg(%s); Mo(%s); Ma(%s)\n     M: Lg(%s); Mo(%s); Ma(%s)\n     F:%s\n     M:%s", 
				eDosha, kuta2Names[11],
				rasiNames[fLagnaRasi], rasiNames[fMoonRasi], rasiNames[fMarsRasi], 
				rasiNames[mLagnaRasi], rasiNames[mMoonRasi], rasiNames[mMarsRasi],  fStr, mStr);
		return tStr;
	}	

	// TODO: Not very useful: Not clear
	public String GetVayanukulya()
	{
		String tStr = "";
		kNoPoints[8] = 0;
		//int [] mStar = mChart.GetPlanetStar(P_MO);
		//int [] fStar = fChart.GetPlanetStar(P_MO);
		//Byte [] vKuta = {0,0,0,0,0, 1,1,1,1,1,1, 2,2,2,2,2,2, 3,3,3,3,3, 4,4,4,4,4};
		
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[8]], "","",kuta2Names[8]);
		return tStr;
	}

	// TODO: Not clear. Need to understand	
	public String GetRnaDhana()
	{
		String tStr = "";
		kNoPoints[10] = 0;
		//int [] mStar = mChart.GetPlanetStar(P_MO);
		//int [] fStar = fChart.GetPlanetStar(P_MO);
		//Byte [] vKuta = {0,0,0,0,0, 1,1,1,1,1,1, 2,2,2,2,2,2, 3,3,3,3,3, 4,4,4,4,4};
		
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[10]], "","",kuta2Names[10]);
		return tStr;
	}	
	

	// TODO: Not clear. not required
	public String GetChittanukulya()
	{
		String tStr = "";
		kNoPoints[9] = 0;
		//int [] mStar = mChart.GetPlanetStar(P_MO);
		//int [] fStar = fChart.GetPlanetStar(P_MO);
		//Byte [] vKuta = {0,0,0,0,0, 1,1,1,1,1,1, 2,2,2,2,2,2, 3,3,3,3,3, 4,4,4,4,4};
		
		tStr = String.format("%4s (%s,%s):%s", sRes[kNoPoints[9]], "","",kuta2Names[9]);
		return tStr;
	}
	

	// Load Kuta details file
    public ArrayList<String> LoadKutaDetails(int kutaId, ArrayList<String> aStr) {
    	String tPath = String.format("%s/k%02d.txt", pPath, kutaId);
    	File f = new File(tPath);
		if (!f.exists()) {
			aStr.add(".");
			aStr.add(".");
           return aStr;
		}
		
		try {
			InputStream is = new FileInputStream(tPath);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
		    String read;
			read = br.readLine();  // Skip this: Kuta name
			read = br.readLine();  // Breif description
			aStr.add(read);
			
			String tStr ="";
			while ((read = br.readLine()) != null) {    // Detailed description
				if (read.contains("=="))  tStr += "\n\n";
				else                      tStr += read;
			}
			aStr.add(tStr);

            is.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return aStr;
    }

}



/*
 * BPHS Analysis relatd marriage
 
http://marriage-matching.blogspot.in/2011/12/marriage-2-brihat-parashara-hora-sastra.html

1. Analysis of individual chart
2. Transits
3. Fixing Marriage date

*/