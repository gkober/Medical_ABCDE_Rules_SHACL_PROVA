package com.spirit.DMRE.DataGenerator;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.util.DateUtils;

public class GeneratedPatient {

	String givenName;
	String PatientFamily;
	String PatientGender;
	String PatientPID;
	String BirthDate;
	String StreetLine;
	String ZIPCode;
	String City;
	String PhoneNumber;
	
	public GeneratedPatient() {
	}
	public void generatePatientData() throws IOException {
		this.PatientGender = this.generatePatientGender();
		this.givenName = this.generatePatientGiven(this.PatientGender);
		this.PatientFamily = this.generatePatientFamily();
		this.PatientPID = this.generatePatientIdentifier(10);
		this.BirthDate = this.generatePatientBirthDate();
		this.StreetLine = this.generatePatientStreet();
		this.ZIPCode = this.generatePatientZip();
		this.City = this.generatePatientCity();
		this.PhoneNumber = this.generatePatientPhone();
	}
	public String generatePatientGiven(String gender) throws IOException {
		if(gender == "M") {
			return Utils.getRandomValueFromFile("givenNamesMale.txt");	  
		}
		if(gender == "F") {
			return Utils.getRandomValueFromFile("givenNamesFemale.txt");  
		}
		else {
			//unkown - could be either a male or female name ... let's select randomly
			Random random = new Random();
			int rdNr = random.nextInt(10);
			if(rdNr%2 == 0) {
				return Utils.getRandomValueFromFile("givenNamesFemale.txt");
			}else {
				return Utils.getRandomValueFromFile("givenNamesMale.txt");	
			}
		}
	}
	public String generatePatientFamily() throws IOException {
			return Utils.getRandomValueFromFile("familyNames.txt");
	}
	public String generatePatientGender() {
		List<String> list = new ArrayList<>();
		list.add("M");
		list.add("F");
		list.add("U");
		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));
	}
	public String generatePatientIdentifier(int size) {
		Random rd = new Random();
		int rdNr =0;
		String[] generatedIdentifier = new String[size];
		StringBuffer sb = new StringBuffer();
		for(int i =0; i<size;i++) {
			rdNr = rd.nextInt(10);
			generatedIdentifier[i] = Integer.toString(rdNr);
		}
		for(int j =0;j<generatedIdentifier.length;j++) {
			sb.append(generatedIdentifier[j]);
		}
		return sb.toString();
	}
	public String generatePatientBirthDate() {
		Random random = new Random();
		int minDay = (int) LocalDate.of(1950, 1, 1).toEpochDay();
		int maxDay = (int) LocalDate.of(2020, 1, 1).toEpochDay();
		long randomDay = minDay + random.nextInt(maxDay - minDay);
		LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
		return randomBirthDate.toString();

    }
	public String generatePatientStreet() throws IOException {
		ArrayList<String> result = new ArrayList<>();
		String currentPath = new java.io.File(".").getCanonicalPath();		
		try(Scanner scanner = new Scanner(new FileReader(currentPath+"/src/main/java/com/spirit/DMRE/DataGenerator/streetNames.txt"))){
			while(scanner.hasNext()) {
				result.add(scanner.nextLine());
			}
		}
		Random random = new Random();
		String streetLine = result.get(random.nextInt(result.size()));
		String streetNumber = Integer.toString(random.nextInt(200));
		return streetLine + " " +streetNumber;
		
	}
	public String generatePatientZip() {
		Random random = new Random();
		String zipCode = Integer.toString(random.nextInt(90000)+9000);
		return zipCode;
	}
	public String generatePatientCity() throws IOException {
		return Utils.getRandomValueFromFile("cities.txt");  
	}
	public String generatePatientPhone() {
		Random rd = new Random();
		int rdNr =0;
		int size = 12;
		String[] generatedIdentifier = new String[size];
		StringBuffer sb = new StringBuffer();
		for(int i =0; i<size;i++) {
			rdNr = rd.nextInt(10);
			generatedIdentifier[i] = Integer.toString(rdNr);
			if(i%4 == 0 && i!=0) {
				generatedIdentifier[i] = "-";
			}
		}
		for(int j =0;j<generatedIdentifier.length;j++) {
			sb.append(generatedIdentifier[j]);
		}
		return sb.toString();
	}
	
	/**
	 * helpermethod to generate FHIR resource out of CSV-file
	 * @return
	 */
	public Patient generateFhirPatient() {
		Patient pat = null;
		pat = new Patient();
		pat.addIdentifier().setSystem("http://acme.org/mrns").setValue(this.PatientPID);
		pat.addName().setFamily(this.PatientFamily).addGiven(this.givenName);
		if(this.PatientGender.equalsIgnoreCase("M")) {
			pat.setGender(Enumerations.AdministrativeGender.MALE);
		}else if(this.PatientGender.equalsIgnoreCase("F")) {
			pat.setGender(Enumerations.AdministrativeGender.FEMALE);
		}else {
			pat.setGender(Enumerations.AdministrativeGender.UNKNOWN);
		}
		pat.setBirthDate(DateUtils.parseDate(this.BirthDate));
		pat.addAddress().addLine(this.StreetLine).setCity(this.City).setPostalCode(this.ZIPCode);
		pat.addTelecom().setSystem(ContactPointSystem.PHONE).setValue(this.PhoneNumber);
		pat.setId(IdType.newRandomUuid());
		//for debugging - printing the resource
		//System.out.println(FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(pat));
		return pat;
	}
	
	
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getPatientFamily() {
		return PatientFamily;
	}
	public void setPatientFamily(String patientFamily) {
		PatientFamily = patientFamily;
	}
	public String getPatientGender() {
		return PatientGender;
	}
	public void setPatientGender(String patientGender) {
		PatientGender = patientGender;
	}
	public String getPatientPID() {
		return PatientPID;
	}
	public void setPatientPID(String patientPID) {
		PatientPID = patientPID;
	}
	public String getBirthDate() {
		return BirthDate;
	}
	public void setBirthDate(String birthDate) {
		BirthDate = birthDate;
	}
	public String getStreetLine() {
		return StreetLine;
	}
	public void setStreetLine(String streetLine) {
		StreetLine = streetLine;
	}
	public String getZIPCode() {
		return ZIPCode;
	}
	public void setZIPCode(String zIPCode) {
		ZIPCode = zIPCode;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getPhoneNumber() {
		return PhoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		PhoneNumber = phoneNumber;
	}

}