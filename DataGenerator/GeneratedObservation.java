package com.spirit.DMRE.DataGenerator;

import java.util.Map;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.StringType;

import ca.uhn.fhir.context.FhirContext;

public class GeneratedObservation {
	String airwayIsFree = null;
	String breathes = null;
	String cyanosis = null;
	String chestExpansion = "normal";
	String respiratoryRhythm = "normal";
	String symmetricChestExpansion = null;
	String pulseCentral = null;
	String pulsePeripheral = null;
	String pupilsIsocore = null;
	String strokeSigns = null;
	String hasBleeding = null;
	String hasBrokenBone = null;
	String hasAllergies = null;
	String hasPain = null;
	int respiratoryRate = 0;
	int oxygenSaturation = 0;
	int glucoseLevel = 0;
	int pulseRate = 0;
	int systolicBloodPressure =0;
	int gcsnumber = 0;
	String expectedResult;
	Utils utils;
	
	
	public GeneratedObservation() {
		utils = new Utils();
	}
	public void printResults() {
		System.out.println("airwayIsFree: " + airwayIsFree);
		System.out.println("breathes: " + breathes);
		System.out.println("cyanosis: " + cyanosis);
		System.out.println("respiratoryRate: " + respiratoryRate);
		System.out.println("chestExpansion: " + chestExpansion);
		System.out.println("respiratoryRhythm: " + respiratoryRhythm);
		System.out.println("symmetricChestExpansion: " + symmetricChestExpansion);
		System.out.println("oxygenSaturation: " + oxygenSaturation);
		System.out.println("pulseRate: " + pulseRate);
		System.out.println("systolicBloodPressure: " + systolicBloodPressure);
		System.out.println("pulseCentral: " + pulseCentral);
		System.out.println("pulsePeripheral: " + pulsePeripheral);
		System.out.println("glucoseLevel: " + glucoseLevel);
		System.out.println("gcsnumber: " + gcsnumber);
		System.out.println("pupilsIsocore: " + pupilsIsocore);
		System.out.println("strokeSigns: " + strokeSigns);
		System.out.println("hasBleeding: " + hasBleeding);
		System.out.println("hasBrokenBone: " + hasBrokenBone);
		System.out.println("hasAllergies: " + hasAllergies);
		System.out.println("hasPain: " + hasPain);
		System.out.println("expectedResult: " + expectedResult);
	}
	public String returnExpectedResult() {
		if( 	airwayIsFree == "YES" && 
				breathes == "YES" &&
				cyanosis == "NO" &&
				respiratoryRate >=8 && respiratoryRate <=15 &&
				chestExpansion == "NORMAL" &&
				respiratoryRhythm == "NORMAL" && 
				symmetricChestExpansion == "YES" &&
				oxygenSaturation >=95 &&
				pulseRate <= 100 && pulseRate >=40 && 
				systolicBloodPressure <=150 && systolicBloodPressure >=100 &&
				pulseCentral == "YES" &&
				pulsePeripheral == "YES" &&
				glucoseLevel <= 200 && glucoseLevel >=50 &&
				gcsnumber >= 6 &&
				pupilsIsocore == "YES" &&
				strokeSigns == "NO" &&
				hasBleeding == "NO" &&
				hasBrokenBone == "NO" &&
				hasAllergies == "NO" &&
				hasPain == "NO") {
				expectedResult = "NOT CRITICAL";
			}
			else {
				expectedResult="CRITICAL";
			}
		return expectedResult;
	}
	public void generatePotentialCriticalIllPatientData() {
		airwayIsFree = this.utils.getRandomTrueFalse();
		if (airwayIsFree == "NO") {
			breathes = "NO";
			cyanosis = "YES";
		}
		if (breathes == "NO") {
			cyanosis = "YES";
			respiratoryRate = 0;
			chestExpansion = "NOT NORMAL";
			respiratoryRhythm = "NO";
			symmetricChestExpansion = "NO";
			oxygenSaturation = this.utils.getRandomNumber(1, 94); 
		}else {
			breathes = "YES";
			cyanosis = this.utils.getRandomTrueFalse();
			respiratoryRate = this.utils.getRandomNumber(1, 40);
			chestExpansion = this.utils.getRandomNormalNotNormal();
			respiratoryRhythm = this.utils.getRandomTrueFalse();
			symmetricChestExpansion = this.utils.getRandomTrueFalse();
			oxygenSaturation = this.utils.getRandomNumber(1, 100);
			if (chestExpansion != "NORMAL") {
				oxygenSaturation = this.utils.getRandomNumber(50, 86);
			}
			if (respiratoryRate <= 8 || respiratoryRate >=20) {
				oxygenSaturation = this.utils.getRandomNumber(50, 86);
			}
		}
		
		pulseRate = this.utils.getRandomNumber(0, 200);
		if(pulseRate == 0) {
			pulsePeripheral = "NO";
			pulseCentral = "NO";
			systolicBloodPressure = 0;
		}
		pulsePeripheral = this.utils.getRandomTrueFalse();
		if(pulsePeripheral == "YES") {
			pulseCentral = "YES";
			systolicBloodPressure = this.utils.getRandomNumber(60, 250);
			gcsnumber = this.utils.getRandomNumber(1, 7);
		}
		else {
			pulseCentral = this.utils.getRandomTrueFalse();
			systolicBloodPressure = this.utils.getRandomNumber(30, 60);
			gcsnumber = this.utils.getRandomNumber(1, 5);
		}
		glucoseLevel = this.utils.getRandomNumber(5, 1000);
		pupilsIsocore = this.utils.getRandomTrueFalse();
		strokeSigns = this.utils.getRandomTrueFalse();
		hasBleeding = this.utils.getRandomTrueFalse();
		hasBrokenBone = this.utils.getRandomTrueFalse();
		hasAllergies = this.utils.getRandomTrueFalse();
		hasPain = this.utils.getRandomTrueFalse();
		expectedResult = this.returnExpectedResult();
	}
	public void generateHealthyPatientData() {
		airwayIsFree = "YES";
		breathes = "YES";
		cyanosis = "NO";
		respiratoryRate = this.utils.getRandomNumber(12, 15);
		chestExpansion = "NORMAL";
		respiratoryRhythm = "NORMAL";
		symmetricChestExpansion = "YES";
		oxygenSaturation = this.utils.getRandomNumber(96, 100);
		pulseRate = this.utils.getRandomNumber(40, 100);
		pulsePeripheral = "YES";
		pulseCentral = "YES";
		systolicBloodPressure = this.utils.getRandomNumber(90, 140);
		gcsnumber = this.utils.getRandomNumber(13, 15);					
		glucoseLevel = this.utils.getRandomNumber(51, 200);
		pupilsIsocore = "YES";
		strokeSigns = "NO";
		hasBleeding = "NO";
		hasBrokenBone = "NO";
		hasAllergies = "NO";
		hasPain = "NO";
		expectedResult = this.returnExpectedResult();
	}
	
	/**
	 * helper-method to generate Observation from CSV-File
	 * 
	 * @return
	 */
	public Observation generateFhirObservation(Object valueToPut, String classFromOntology) {
		//System.out.println("generateFhirObservation -method called");
		Observation obs = new Observation();
		String setCode = null;
		String setDisplay = null;
		String setUnit = null;
		//System.out.println("ValueToPut: " + valueToPut);
		//System.out.println("classFromOntology: " + classFromOntology);
		//System.out.println("ClassFromOntology - needed for query: " + classFromOntology +"\n");
		Map <String,String> fillMap = Utils.queryBioPortalOntologies(classFromOntology);
		setCode = fillMap.get("http://www.w3.org/2004/02/skos/core#notation").toString();
		setCode = setCode.replaceAll("\\[", "").replaceAll("\\]","");
		setCode = setCode.replaceAll("\"","");
		setDisplay = fillMap.get("http://www.w3.org/2004/02/skos/core#prefLabel").toString();
		setDisplay = setDisplay.replaceAll("\\[", "").replaceAll("\\]","");
		setDisplay = setDisplay.replaceAll("\"","");
		setUnit = fillMap.get("http://purl.bioontology.org/ontology/LNC/EXAMPLE_UNITS").toString();
		setUnit = setUnit.replaceAll("\\[", "").replaceAll("\\]","");
		setUnit = setUnit.replaceAll("\"","");
		System.out.println("VALUETOPUT: " + valueToPut);
		//This block is used to grab possible values from another list ... 
//		ToCodeMapper tcm = new ToCodeMapper();
//		String fillValue = tcm.returnMapValue(classFromOntology, valueToPut.toString());
//		if(fillValue != null) {
//			System.out.println("Try to get posisble values");
//			System.out.println(fillValue);
//			valueToPut = fillValue;
//		}else {
//			System.out.println("no possible mapping found, proceed with provided values");
//		}
		//additional block ending ... 
		
		
		if(Utils.isBoolean(valueToPut.toString())) {
			//String valueToPut1 = tcm.returnairwayMap(valueToPut.toString());
			obs.setValue(new BooleanType().setValue(Utils.changeToBoolean(valueToPut.toString())));
		}
		else if(Utils.isNumeric(valueToPut.toString())) {			
			obs.setValue(new Quantity().setValue(Integer.parseInt((String) valueToPut)).setUnit(setUnit).setSystem("http://unitsofmeasure.org").setCode(setCode));
		}
		else if(!Utils.isNumeric(valueToPut.toString())) {
			obs.setValue(new StringType((String) valueToPut));
		}
		if(classFromOntology.contains("-")) {
			//adding-loing code system
			obs.getCode().addCoding().setSystem("http://loinc.org").setCode(setCode).setDisplay(setDisplay);
		}else {
			//adding snomed code system
			obs.getCode().addCoding().setSystem("http://snomed.info").setCode(setCode).setDisplay(setDisplay);
		}
		
		obs.setStatus(Observation.ObservationStatus.FINAL);
		//System.out.println(FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(obs));
		return obs;		
	}
	public String getAirwayIsFree() {
		return airwayIsFree;
	}
	public String getBreathes() {
		return breathes;
	}
	public String getCyanosis() {
		return cyanosis;
	}
	public String getChestExpansion() {
		return chestExpansion;
	}
	public String getRespiratoryRhythm() {
		return respiratoryRhythm;
	}
	public String getsymmetricChestExpansion() {
		return symmetricChestExpansion;
	}
	public String getPulseCentral() {
		return pulseCentral;
	}
	public String getPulsePeripheral() {
		return pulsePeripheral;
	}
	public String getPupilsIsocore() {
		return pupilsIsocore;
	}
	public String getStrokeSigns() {
		return strokeSigns;
	}
	public String getHasBleeding() {
		return hasBleeding;
	}
	public String getHasBrokenBone() {
		return hasBrokenBone;
	}
	public String getHasAllergies() {
		return hasAllergies;
	}
	public String getHasPain() {
		return hasPain;
	}
	public String getRespiratoryRate() {
		return Integer.toString(respiratoryRate);
	}
	public String getOxygenSaturation() {
		return Integer.toString(oxygenSaturation);
	}
	public String getGlucoseLevel() {
		return Integer.toString(glucoseLevel);
	}
	public String getPulseRate() {
		return Integer.toString(pulseRate);
	}
	public String getSystolicBloodPressure() {
		return Integer.toString(systolicBloodPressure);
	}
	public String getGcsnumber() {
		return Integer.toString(gcsnumber);
	}
	public String getExpectedResult() {
		return expectedResult;
	}
	public void setAirwayIsFree(String airwayIsFree) {
		this.airwayIsFree = airwayIsFree;
	}
	public void setBreathes(String breathes) {
		this.breathes = breathes;
	}
	public void setCyanosis(String cyanosis) {
		this.cyanosis = cyanosis;
	}
	public void setChestExpansion(String chestExpansion) {
		this.chestExpansion = chestExpansion;
	}
	public void setRespiratoryRhythm(String respiratoryRhythm) {
		this.respiratoryRhythm = respiratoryRhythm;
	}
	public void setsymmetricChestExpansion(String symmetricChestExpansion) {
		this.symmetricChestExpansion = symmetricChestExpansion;
	}
	public void setPulseCentral(String pulseCentral) {
		this.pulseCentral = pulseCentral;
	}
	public void setPulsePeripheral(String pulsePeripheral) {
		this.pulsePeripheral = pulsePeripheral;
	}
	public void setPupilsIsocore(String pupilsIsocore) {
		this.pupilsIsocore = pupilsIsocore;
	}
	public void setStrokeSigns(String strokeSigns) {
		this.strokeSigns = strokeSigns;
	}
	public void setHasBleeding(String hasBleeding) {
		this.hasBleeding = hasBleeding;
	}
	public void setHasBrokenBone(String hasBrokenBone) {
		this.hasBrokenBone = hasBrokenBone;
	}
	public void setHasAllergies(String hasAllergies) {
		this.hasAllergies = hasAllergies;
	}
	public void setHasPain(String hasPain) {
		this.hasPain = hasPain;
	}
	public void setRespiratoryRate(int respiratoryRate) {
		this.respiratoryRate = respiratoryRate;
	}
	public void setOxygenSaturation(int oxygenSaturation) {
		this.oxygenSaturation = oxygenSaturation;
	}
	public void setGlucoseLevel(int glucoseLevel) {
		this.glucoseLevel = glucoseLevel;
	}
	public void setPulseRate(int pulseRate) {
		this.pulseRate = pulseRate;
	}
	public void setSystolicBloodPressure(int systolicBloodPressure) {
		this.systolicBloodPressure = systolicBloodPressure;
	}
	public void setGcsnumber(int gcsnumber) {
		this.gcsnumber = gcsnumber;
	}
	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

}
