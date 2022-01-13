package com.spirit.DMRE.DataGenerator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.aopalliance.reflect.Field;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import static org.eclipse.rdf4j.model.util.Values.iri;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

/**
 * 
 * @author gerhard
 * generating (random) samples for ABCDE approach,and writing them to a csv-file
 */
public class generateObservationSamples {
	static Utils utils = new Utils();
	static Boolean submitFhirResourcesToFhirStore_boolean = false;
	static Boolean createFhirResources_boolean = false;
	static String FhirStoreURL = "http://hapi.fhir.org/baseR4";
	static String fileNameForFhirBundle = null;
	static Boolean storeRDFRepresentation_boolean = null;
	static Boolean storeRDFRepresentationAsRDFXML_boolean = null;
	public generateObservationSamples() {
	}
	public static void main(String[] args) throws IOException, NoSuchMethodException, SecurityException {
		int numberOfLinesToGenerate = 0;
		String fileNameForFinalCSV = null;
		String createFhirResources = "no";
		String submitFhirResourcesToFhirStore = "no";
		String storeRDFRepresentation = "yes";
		System.out.println("Generation of Samples for ABCDE approach");
		if(args.length < 2) {
			System.out.println("please provide a number of datasets to be generated");
			System.out.println("Arguments: #NumberOfLinestoGenerate #fileNameForFinalCSV #createFhirResources(yes/no) #fileNameForFhirBundle #submitFhirResourcesToFhirStore(yes/no) FhirStoreURL #storeRDFRepresentation(yes/no");
			System.exit(1);
		}
		try {
			numberOfLinesToGenerate = Integer.parseInt(args[0]);
		}catch(Exception e) {
			System.out.println("Please enter a number");
			System.exit(1);
		}
		if(!args[1].isEmpty()){
			fileNameForFinalCSV = args[1];
		}else {
			System.out.println("Please enter a FileName");
			System.exit(1);
		}
		try {
			submitFhirResourcesToFhirStore = args[4];
			createFhirResources = args[2];
			fileNameForFhirBundle = args[3];
			FhirStoreURL = args[5];
			storeRDFRepresentation = args[6];
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("i have the following parameters: \n" +
							"Number of lines to generate: " + numberOfLinesToGenerate + "\n" +
							"Filename of final CSV: " + fileNameForFinalCSV + "\n" + 
							"CreateFhirResources: " + createFhirResources + "\n" + 
							"FileNameForFhirBundle: " + fileNameForFhirBundle + "\n" + 
							"SubmitFhirResourcesToFhirStore: " + submitFhirResourcesToFhirStore + "\n" +
							"FhirStoreURL: " + FhirStoreURL + "\n"	+
							"storeRDFRepresentation: " + storeRDFRepresentation + "\n"
				);
		if(!submitFhirResourcesToFhirStore.isEmpty()){
			if(submitFhirResourcesToFhirStore.equalsIgnoreCase("yes")) {
				submitFhirResourcesToFhirStore_boolean = true;
			}else if(submitFhirResourcesToFhirStore.equalsIgnoreCase("no")){
				submitFhirResourcesToFhirStore_boolean=false;
			}else {
				System.out.println("no valid value");
				System.exit(1);
			}
		}
		if(!createFhirResources.isEmpty()) {
			if(createFhirResources.equalsIgnoreCase("yes")) {
				createFhirResources_boolean=true;
			}else if(createFhirResources.equalsIgnoreCase("no")){
				createFhirResources_boolean=false;
			}else {
				System.out.println("no valid value");
				System.exit(1);
			}
			if(storeRDFRepresentation.equalsIgnoreCase("yes")) {
				storeRDFRepresentation_boolean = true;
				storeRDFRepresentationAsRDFXML_boolean = true;
			}else if (storeRDFRepresentation.equalsIgnoreCase("no")){
				storeRDFRepresentation_boolean=false;
				storeRDFRepresentationAsRDFXML_boolean = false;
			}else {
				System.out.println("no applicable value found for storing RDF-Representation - do not store it");
				storeRDFRepresentation_boolean=false;
			}
		}
		
		
		boolean healthyPatients = true;
		Random randomBoolean = new Random();
		int counterHealthyPatients = 0;
		int counterCriticalPatients = 0;
		GeneratedPatient patient = new GeneratedPatient();
		GeneratedObservation observation = new GeneratedObservation();
			
		for(int i = 0;i <numberOfLinesToGenerate; i++) {
				patient.generatePatientData();
				healthyPatients = randomBoolean.nextBoolean();
				System.out.println("HealthyPatients: " + healthyPatients);
				if(numberOfLinesToGenerate/2 == counterCriticalPatients) {
					System.out.println("switching critical");
					healthyPatients = !healthyPatients;
				}
				if(numberOfLinesToGenerate/2 == counterHealthyPatients) {
					System.out.println("switching healthy");
					healthyPatients = !healthyPatients;
				}
				if(healthyPatients) {
					counterHealthyPatients++;
					observation.generateHealthyPatientData();
				}else {
					counterCriticalPatients++;
					observation.generatePotentialCriticalIllPatientData();
				}
				
				utils.dataLine.add(new String[] {
						patient.getPatientPID(),
						patient.getGivenName(),
						patient.getPatientFamily(),
						patient.getPatientGender(),
						patient.getBirthDate(),
						patient.getStreetLine(),
						patient.getZIPCode(),
						patient.getCity(),
						patient.getPhoneNumber(),
						
						//now the observationdata
						observation.getAirwayIsFree(),
						observation.getBreathes(),
						observation.getCyanosis(),
						observation.getRespiratoryRate(),
						observation.getChestExpansion(),
						observation.getRespiratoryRhythm(),
						observation.getsymmetricChestExpansion(),
						observation.getOxygenSaturation(),
						observation.getPulseRate(),
						observation.getSystolicBloodPressure(),
						observation.getPulseCentral(),
						observation.getPulsePeripheral(),
						observation.getGlucoseLevel(),
						observation.getGcsnumber(),
						observation.getPupilsIsocore(),
						observation.getStrokeSigns(),
						observation.getHasBleeding(),
						observation.getHasBrokenBone(),
						observation.getHasAllergies(),
						observation.getHasPain(),
						observation.getExpectedResult()
						
				});		
				
		}
		try (PrintWriter writer = new PrintWriter(new File(fileNameForFinalCSV))) {
			StringBuilder sb = new StringBuilder();
			sb.append("patientID");sb.append(',');
			sb.append("givenName");sb.append(',');
			sb.append("PatientFamily");sb.append(',');
			sb.append("PatientGender");sb.append(',');
			sb.append("PatientBirthDate");sb.append(',');
			sb.append("PatientStreetLine");sb.append(',');
			sb.append("PatientZIPCode");sb.append(',');
			sb.append("PatientCity");sb.append(',');
			sb.append("PatientPhoneNumber");sb.append(',');
			sb.append("airwayIsFree");sb.append(',');
			sb.append("breathes");sb.append(',');
			sb.append("cyanosis");sb.append(',');
			sb.append("respiratoryRate");sb.append(',');
			sb.append("chestExpansion");sb.append(',');
			sb.append("respiratoryRhythm");sb.append(',');
			sb.append("chestExpansionSymmetric");sb.append(',');
			sb.append("oxygenSaturation");sb.append(',');
			sb.append("pulseRate");sb.append(',');
			sb.append("systolicBloodPressure");sb.append(',');
			sb.append("pulseCentral");sb.append(',');
			sb.append("pulsePeripheral");sb.append(',');
			sb.append("glucoseLevel");sb.append(',');
			sb.append("gcsnumber");sb.append(',');
			sb.append("pupilsIsocore");sb.append(',');
			sb.append("strokeSigns");sb.append(',');
			sb.append("hasBleeding");sb.append(',');
			sb.append("hasBrokenBone");sb.append(',');
			sb.append("hasAllergies");sb.append(',');
			sb.append("hasPain");sb.append(',');
			sb.append("ExpectedResult");sb.append('\n');
			for(int k = 0; k<utils.getDataLine().size();k++)
			{	
				for(String s : utils.getDataLine().get(k)) {
					sb.append(s);sb.append(',');
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append('\n');
			}		
			writer.write(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Generation completed");
		if(createFhirResources_boolean==true) {
			readCSVandTriggerFHIRGeneration(fileNameForFinalCSV);
		}else {
			System.out.println("Exiting - CSV-generation done");
		}
	}
	
	public static void readCSVandTriggerFHIRGeneration(String CSVFile) throws IOException, NoSuchMethodException, SecurityException {
		BufferedReader csvReader = new BufferedReader(new FileReader(CSVFile));
		GeneratedPatient patient = new GeneratedPatient();
		GeneratedObservation observation = new GeneratedObservation();
		String row;
		int rowCounter =0;
		while ((row = csvReader.readLine()) != null) {
			if(rowCounter == 0) {
				row = csvReader.readLine();
			}
			rowCounter++;
			System.out.println("Rownumber in CSV: " + rowCounter);
		    String[] data = row.split(",");
		    for(int i=0; i<data.length;i++) {
		    	//set PatientData to the object
		    	patient.setPatientPID(data[0]);
		    	patient.setGivenName(data[1]);
		    	patient.setPatientFamily(data[2]);
		    	patient.setPatientGender(data[3]);
		    	patient.setBirthDate(data[4]);
		    	patient.setStreetLine(data[5]);
		    	patient.setZIPCode(data[6]);
		    	patient.setCity(data[7]);
		    	patient.setPhoneNumber(data[8]);
		    	
		    	//set ObservationData to the object
		    	observation.setAirwayIsFree(data[9]);
				observation.setBreathes(data[10]);
				observation.setCyanosis(data[11]);
				observation.setRespiratoryRate(Integer.parseInt(data[12]));
				observation.setChestExpansion(data[13]);
				observation.setRespiratoryRhythm(data[14]);
				observation.setsymmetricChestExpansion(data[15]);
				observation.setOxygenSaturation(Integer.parseInt(data[16]));
				observation.setPulseRate(Integer.parseInt(data[17]));
				observation.setSystolicBloodPressure(Integer.parseInt(data[18]));
				observation.setPulseCentral(data[19]);
				observation.setPulsePeripheral(data[20]);
				observation.setGlucoseLevel(Integer.parseInt(data[21]));
				observation.setGcsnumber(Integer.parseInt(data[22]));
				observation.setPupilsIsocore(data[23]);
				observation.setStrokeSigns(data[24]);
				observation.setHasBleeding(data[25]);
				observation.setHasBrokenBone(data[26]);
				observation.setHasAllergies(data[27]);
				observation.setHasPain(data[28]);
		    }
			generateFhirRequest(observation,patient);
		}
		csvReader.close();
	}
	public static void generateFhirRequest(GeneratedObservation observation, GeneratedPatient patient) throws NoSuchMethodException, SecurityException {
		//create the FHIR Patient and FHIR Observation out of the csv-data
		Patient pat = patient.generateFhirPatient();
		List<Observation> allPatientObservations = new ArrayList<Observation>();		
		for(java.lang.reflect.Field f : observation.getClass().getDeclaredFields()) {
			for(Method methods : observation.getClass().getDeclaredMethods()) {
				if(methods.getName().matches("(?i).*get"+f.getName()+".*") && !(methods.getName().equalsIgnoreCase("getExpectedResult"))) {
					Method toCall = observation.getClass().getMethod(methods.getName());
					try {	
						//System.out.println("utils.getName: " + methods.getName());
						//System.out.println("utils.getMappingNameToCode: " + utils.getMappingNameToCode().get(f.getName()));
						allPatientObservations.add(observation.generateFhirObservation(toCall.invoke(observation), utils.getMappingNameToCode().get(f.getName())));
					}catch(Exception e) {
						e.printStackTrace();
						continue;
					}
					
				}
			}
		}
		//create a bundle out of patient and observations...
		Bundle bundle = new Bundle();
		bundle.setType(Bundle.BundleType.TRANSACTION);
		bundle.addEntry().setFullUrl(pat.getIdElement().getValue()).setResource(pat).getRequest().setUrl("Patient").setMethod(Bundle.HTTPVerb.POST);
		for(Observation obs : allPatientObservations) {
			obs = obs.setSubject(new Reference("Patient/"+pat.getIdElement().getValue()));
			bundle.addEntry().setResource(obs).getRequest().setUrl("Observation").setMethod(Bundle.HTTPVerb.POST);
		}
		bundle.setTimestamp(new Date());
		FhirContext ctx = FhirContext.forR4();
		if(!fileNameForFhirBundle.isEmpty()) {
			System.out.println("Saving to: " + fileNameForFhirBundle+pat.getNameFirstRep().getFamily().toString()+pat.getNameFirstRep().getGivenAsSingleString().toString());
			String forStoringFhir = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
			//only for debugging
//			try (PrintWriter out = new PrintWriter(fileNameForFhirBundle+"_"+pat.getNameFirstRep().getFamily().toString()+"_"+pat.getNameFirstRep().getGivenAsSingleString().toString())) {
//			    out.println(forStoringFhir);
//			} catch (FileNotFoundException e) {
//				System.out.println("An error happened during FHIR-Bundle Storage....");
//				e.printStackTrace();
//			}
		}
		// Create a client and post the transaction to the server
		if(submitFhirResourcesToFhirStore_boolean == true) {
			IGenericClient client = ctx.newRestfulGenericClient(FhirStoreURL);
			Bundle resp = client.transaction().withBundle(bundle).execute();
			// Log the response
			//System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
		}
		if(storeRDFRepresentation_boolean == true) {
			//trying to create an TTL-Model ... (every bundle gets another rdf-file)
			String rdfModelString = (FhirContext.forR4().newRDFParser().encodeResourceToString(bundle));
		
			FileOutputStream outputStream = null;
		
			try {
				outputStream = new FileOutputStream("rdf_representation"+"_"+pat.getNameFirstRep().getFamily().toString()+"_"+pat.getNameFirstRep().getGivenAsSingleString().toString()+".rdf");
				outputStream.write(rdfModelString.getBytes(Charset.forName("UTF-8")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					outputStream.close();
					//pimp the TTL with the FHIR.ttl import
					org.apache.jena.rdf.model.Model model=ModelFactory.createDefaultModel();
					OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
					Ontology ont = ontModel.createOntology("http://www.abcde.org/ABox");
				
					ontModel.read(new FileInputStream("rdf_representation"+"_"+pat.getNameFirstRep().getFamily().toString()+"_"+pat.getNameFirstRep().getGivenAsSingleString().toString()+".rdf"),null,"TTL");
					ont.addImport(ontModel.getResource("http://hl7.org/fhir/fhir.ttl"));
					OutputStream modelOut = new FileOutputStream("ttl_representation"+"_"+pat.getNameFirstRep().getFamily().toString()+"_"+pat.getNameFirstRep().getGivenAsSingleString().toString()+".ttl");
					RDFDataMgr.write(modelOut, ontModel, Lang.TTL);
				 
				    //transforming from TTL to RDF-XML
				    InputStream in = new FileInputStream("ttl_representation"+"_"+pat.getNameFirstRep().getFamily().toString()+"_"+pat.getNameFirstRep().getGivenAsSingleString().toString()+".ttl");
				    org.apache.jena.rdf.model.Model modelTransform = ModelFactory.createDefaultModel(); // creates an in-memory Jena Model
				    modelTransform.read(in, null, "TURTLE"); // parses an InputStream assuming RDF in Turtle format
				    OutputStream modelOutRDFXML = new FileOutputStream("rdf_representation"+"_"+pat.getNameFirstRep().getFamily().toString()+"_"+pat.getNameFirstRep().getGivenAsSingleString().toString()+".rdf.xml");
					modelTransform.write(modelOutRDFXML, "RDF/XML");
					ctx = null;
					model=null;
					ontModel=null;
					modelTransform=null;
					System.gc();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
