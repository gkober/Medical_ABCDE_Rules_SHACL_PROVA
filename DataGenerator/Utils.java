package com.spirit.DMRE.DataGenerator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * here are Utils for randomGeneration of Numbers, YES-no-values, Normal-notNormal
 * also the csv-writer is located here...
 * @author gerhard
 *
 */
public class Utils {
		public Utils(){
			fillMap_nameToCode();
		}

	    //dataline contains the entire dataset including patient-data & health-data
		public List<String[]> dataLine = new ArrayList<>();
		public StringBuilder sb = new StringBuilder(); //helper for filling the dataLine
		static final ObjectMapper mapper = new ObjectMapper();
		Map<String, String> mappingNameToCode= new HashMap<String,String>();
		
		public int getRandomNumber(int min, int max) {
			Random rand = new Random();
			return rand.nextInt((max - min) + 1) + min;
		}
		public String getRandomTrueFalse() {
			Random rand = new Random();
			boolean result = rand.nextBoolean();
			if(result == true) {
				return "YES";
			}else {
				return "NO";
			}
		}
		public String getRandomNormalNotNormal() {
			Random rand = new Random();
			boolean result = rand.nextBoolean();
			if(result == true) {
				return "NORMAL";
			}else {
				return "NOT NORMAL";
			}
		}
		public static boolean isNumeric(String strNum) {
			boolean returnValue;
		    if (strNum.isEmpty()) {
		        return false;
		    }
		    try {
		        double d = Double.parseDouble(strNum);
		        returnValue = true;
		    } catch (NumberFormatException nfe) {
		    	returnValue = false;
		        //return false;
		    }
		    return returnValue;
		}
		public static boolean isBoolean(String stringBoolean) {	
		    if (stringBoolean.isEmpty()) {
		        return false;
		    }
		    if(stringBoolean.equalsIgnoreCase("yes") || stringBoolean.equalsIgnoreCase("no")) {
		    	stringBoolean="true";
		    }
		    try {
		        Boolean b = Boolean.parseBoolean(stringBoolean);
		        if(b == true) {
		        	return true;
		        }
		        else {
		        	return false;
		        }
			    } catch (Exception e) {
			        return false;
			    }
		}
		public static boolean changeToBoolean(String stringBoolean) {
		    if (stringBoolean.isEmpty()) {
		        return false;
		    }
		    if(stringBoolean.equalsIgnoreCase("YES") || stringBoolean.equalsIgnoreCase("NO")) {
		    	//set yes & no to true/false for returnvalues
		    	if(stringBoolean.equalsIgnoreCase("YES")) {
		    		stringBoolean="true";
		    	}
		    	if(stringBoolean.equalsIgnoreCase("NO")) {
		    		stringBoolean="false";
		    	}
		    }
		    try {
		        Boolean b = Boolean.parseBoolean(stringBoolean);
		        if(b == true) {
		        	return true;
		        }else {
		        	return false;
		        }
		    } catch (Exception e) {
		        return false;
		    }
		}
		//Down here is everything for asking the BioPortalOntologies for particular information
		public static Map<String,String> queryBioPortalOntologies(String loincCode) {
			//returningMap
			Map <String,String> returnMap = new HashMap<String,String>();
			String API_KEY = "_to_fill_in_but_get_first_from_bioportal";
			URL url;
			HttpURLConnection conn;
	        BufferedReader rd;
	        String line;
	        String result = "";
	        //System.out.println("LoincCode in Utils is: " + loincCode + "\n\n");
	        try {
	        	if(loincCode.contains("-")) {
	        		url = new URL("http://data.bioontology.org/ontologies/LOINC/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FLNC%2F"+loincCode+"?include=properties");      	
	        	}else {
	        		url = new URL("http://data.bioontology.org/ontologies/SNOMEDCT/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FSNOMEDCT%2F"+loincCode+"?include=properties");      	
	        	}
	        	conn = (HttpURLConnection) url.openConnection();
	        	conn.setRequestMethod("GET");
	        	conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
	        	conn.setRequestProperty("Accept", "application/json");
	        	InputStreamReader inputStream = new InputStreamReader(conn.getInputStream());
	        	rd = new BufferedReader(inputStream);
	        	while ((line = rd.readLine()) != null) {
	        		result += line;
	        	}
	        	rd.close();
	        	JsonNode searchResult = jsonToNode(result);
	        	returnMap.put("http://www.w3.org/2004/02/skos/core#prefLabel", searchResult.findValues("http://www.w3.org/2004/02/skos/core#prefLabel").toString());
	        	returnMap.put("http://www.w3.org/2004/02/skos/core#notation", searchResult.findValues("http://www.w3.org/2004/02/skos/core#notation").toString());
	        	if(searchResult.findValues("http://purl.bioontology.org/ontology/LNC/EXAMPLE_UNITS") != null)
	        		returnMap.put("http://purl.bioontology.org/ontology/LNC/EXAMPLE_UNITS", searchResult.findValues("http://purl.bioontology.org/ontology/LNC/EXAMPLE_UNITS").toString());
				}catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        return returnMap;
			
		}
		//helper-Method for the BioPortal-Query
		private static JsonNode jsonToNode(String json) {
			JsonNode root = null;
			try {
				root = mapper.readTree(json);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return root;
		}
		private void fillMap_nameToCode() {
			this.mappingNameToCode.put("airwayIsFree", "69046-1"); //Airway status
			this.mappingNameToCode.put("breathes", "9278-3"); //Sleeping,Post exercise,Apnea,Crying,Other,Post inhalation Rx,Fever,Pain,Pre exercise
			this.mappingNameToCode.put("cyanosis", "39107-8"); //Color of the skin Has answer 	:Mottled,Jaundice,Lividity,Normal,Flushed,Pale,Cyanotic,Red (erythematous)
			this.mappingNameToCode.put("respiratoryRate", "9279-1");
			this.mappingNameToCode.put("chestExpansion", "67528-0"); //not sure if right/useful code...
			this.mappingNameToCode.put("respiratoryRhythm", "9304-7"); //Unknown,Hyperventilation,Biot's,Apnea,Irregular Breathing,Not assessed,Sighing,Cluster Breathing,Ventilation,Normal breathing,Other pathological breathing pattern or cluster,Air Hunger,Dyspnea,Apneustic,Cheyne-Stokes,Acidotic Hyperventilation,Gasping for Air
			this.mappingNameToCode.put("symmetricChestExpansion", "248562002"); //http://purl.bioontology.org/ontology/SNOMEDCT/248562002 -- Paradoxical chest movement
			this.mappingNameToCode.put("oxygenSaturation", "20564-1");
			this.mappingNameToCode.put("pulseRate", "8867-4");
			this.mappingNameToCode.put("systolicBloodPressure", "8480-6");
			this.mappingNameToCode.put("pulseCentral", "8900-3");  //Pulse intensity Carotid artery - left by palpation
			this.mappingNameToCode.put("pulsePeripheral", "8911-0");//Pulse intensity Radial artery - right by palpation
			this.mappingNameToCode.put("glucoseLevel", "2339-0"); //Glucose [Mass/volume] in Blood
			this.mappingNameToCode.put("gcsnumber", "35088-4");  //Glasgow coma scale
			this.mappingNameToCode.put("pupilsIsocore", "80313-0"); //Pupil assessment
			this.mappingNameToCode.put("strokeSigns", "72089-6"); //Total score [NIH Stroke Scale]
			this.mappingNameToCode.put("hasBleeding", "81661-1"); //Bld loss Vol Measured
			this.mappingNameToCode.put("hasBrokenBone", "66571-1"); //Broken bone - [PhenX]Trial
			this.mappingNameToCode.put("hasAllergies", "52571-7"); //Does patient have allergies or any known adverse drug reactions?
			this.mappingNameToCode.put("hasPain", "52604-6"); //Pain Presence. Ask patient: "Have you had pain or hurting at any time during the last 2 days?"
			
		}
		public Map<String, String> getMappingNameToCode() {
			return mappingNameToCode;
		}
		public List<String[]> getDataLine(){
			return dataLine;
		}
		public static String getRandomValueFromFile(String filename) throws IOException {
			ArrayList<String> result = new ArrayList<>();
			Random random = new Random();
			String currentPath = new java.io.File(".").getCanonicalPath();		
			try(Scanner scanner = new Scanner(new FileReader(currentPath+"/src/main/java/com/spirit/DMRE/DataGenerator/"+filename))){
				while(scanner.hasNext()) {
					result.add(scanner.nextLine());
				}
			}
			String returnString = result.get(random.nextInt(result.size()));
			return returnString;
		}
		public static void convertTTLtoRDFXML(FileInputStream inputStream, String fileName) throws RDFParseException, RDFHandlerException, IOException {
			int lastDotOfFilename = fileName.lastIndexOf(".");
			String ext = "";
			if (lastDotOfFilename > 0 &&  lastDotOfFilename < fileName.length() - 1) {
			      ext = fileName.substring(lastDotOfFilename + 1);
			    }
			fileName = fileName.replaceFirst("."+ext+ "$", Matcher.quoteReplacement("."+"xml"));
			RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
			RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, new FileOutputStream(fileName));
			parser.setRDFHandler(writer);
			parser.parse(inputStream);
		}
		public static void convertRDFXMLtoTTL(FileInputStream inputStream, String fileName) throws UnsupportedRDFormatException, RDFParseException, RDFHandlerException, IOException {
			int lastDotOfFilename = fileName.lastIndexOf(".");
			String ext = "";
			if (lastDotOfFilename > 0 &&  lastDotOfFilename < fileName.length() - 1) {
			      ext = fileName.substring(lastDotOfFilename + 1);
			    }
			fileName = fileName.replaceFirst("."+ext+ "$", Matcher.quoteReplacement("."+"ttl"));
			RDFParser parser = Rio.createParser(RDFFormat.RDFXML);
			RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, new FileOutputStream(fileName));
			parser.setRDFHandler(writer);
			parser.parse(inputStream);
		}
}
