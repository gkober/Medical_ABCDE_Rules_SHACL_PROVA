package com.spirit.DMRE.DataGenerator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class ToCodeMapper {

	Table<String, String, String> codeTable = HashBasedTable.create();
	
/**
 * 			"airwayIsFree -> 			69046-1
			"breathes -> 				9278-3
			"cyanosis -> 				39107-8
			"respiratoryRate -> 		9279-1
			"chestExpansion -> 			67528-0
			"respiratoryRhythm -> 		9304-7
			"symmetricChestExpansion -> 248562002
			"oxygenSaturation -> 		20564-1"
			"pulseRate -> 				8867-4"
			"systolicBloodPressure -> 	8480-6"
			"pulseCentral -> 			8900-3"
			"pulsePeripheral -> 		8911-0"
			"glucoseLevel -> 			2339-0
			"gcsnumber -> 				35088-4"
			"pupilsIsocore -> 			80313-0
			"strokeSigns -> 			72089-6
			"hasBleeding -> 			81661-1
			"hasBrokenBone -> 			66571-1
			"hasAllergies -> 			52571-7
			"hasPain -> 				52604-6
			
 */
	public ToCodeMapper() {
		//this.codeTable.put("", "", ""); --> this is the template for later
		System.out.println("new toCodeMapper; trying to fill the table");
		//airway
		this.codeTable.put("69046-1","NO", "LA18010-1");
		this.codeTable.put("69046-1","YES", "LA18011-9");
		//breathing
		this.codeTable.put("9278-3", "true", "LA46-8");
		this.codeTable.put("9278-3", "false", "LA17393-2");
		//cyanosis
		this.codeTable.put("39107-8", "YES", "LA17198-5");
		this.codeTable.put("39107-8", "NO", "LA6626-1");
		//chestExpansion
		this.codeTable.put("67528-0", "NORMAL", "LA6626-1");
		this.codeTable.put("67528-0", "NOT NORMAL", "LA18225-5");
		//respiratoryRhythm
		this.codeTable.put("9304-7", "NOT NORMAL", "LA18011-9");
		//symmetricChestExpansion
		this.codeTable.put("248562002", "YES", "366128006");
		//this.codeTable.put("248562002", "NO", ""); // -> no needs definition
		//pulseCentral
		this.codeTable.put("8900-3", "", "");
		//pulsePeripheral
		this.codeTable.put("8911-0", "", "");
		//pupilsIsocore
		this.codeTable.put("80313-0", "YES", "LA25085-4");
		this.codeTable.put("80313-0", "NO", "LA46-8");
		//strokeSigns
		this.codeTable.put("72089-6", "", "");
		//hasBleeding
		this.codeTable.put("81661-1", "", "");
		//hasBrokenBone
		this.codeTable.put("66571-1", "", "");
		//hasAllergies
		this.codeTable.put("52571-7", "YES", "LA33-6");
		this.codeTable.put("52571-7", "NO", "LA10030-7");
		//hasPain
		this.codeTable.put("52604-6", "YES", "LA33-6");
		this.codeTable.put("52604-6", "NO", "LA32-8");		
	}
	public String returnMapValue(String code, String valueToPut ) {
		System.out.println(code);
		System.out.println(valueToPut);
		String returnValue = this.codeTable.get(code, valueToPut);
		System.out.println(returnValue);
		return returnValue;
	}

}
