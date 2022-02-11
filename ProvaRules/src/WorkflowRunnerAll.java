package com.spirit.DMRE.DataGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class WorkflowRunnerAll {

	static String resultToStore;
	static int counter=0;
	String fileName;
	static PrintWriter writerCSV;
	public WorkflowRunnerAll() {

	}

	public static void main(String[] args) throws IOException {
		long startEntireWorkflow = System.nanoTime();
		WorkflowRunnerAll runner = new WorkflowRunnerAll();
		//clear resultFile first:
		writerCSV = new PrintWriter(new FileOutputStream(
			    new File("DMRE/100_Package/WorkflowRunnerResult.csv")));
		writerCSV.append("");
		writerCSV.flush();
		writerCSV.close();
		runner.runOverAllFiles();
		long stopEntireWorkflow = System.nanoTime();
		System.out.println((stopEntireWorkflow - startEntireWorkflow)/ 1000000);

	}
	public void runOverAllFiles() throws IOException {
		File folder = new File("DMRE/100_Package/");
		for(File file : folder.listFiles()) {
			if(file.getName().endsWith("xml")) {
			//if(file.getName().contains("Howell_Eliseo.rdf.xml")) {
				System.out.println(file.getName());
				long start = System.nanoTime();
				sendRDFtoStore(file.getName());
				long startProvaExecution = System.nanoTime();
				executeProvaGuideline();
				long endProvaExecution = System.nanoTime();
				removeRDFfromStore();
				long end = System.nanoTime();
				long elapsedTime = (end - start)/ 1000000;
				System.out.println(this.resultToStore);
				long elapsedTimeProvaExecution = (endProvaExecution - startProvaExecution)/ 1000000;
				writeInformationToCSV(file.getName(), this.resultToStore, elapsedTimeProvaExecution, elapsedTime);

				System.out.println("Time spent:" + elapsedTime + " milliseconds");
				System.out.println("Time spent ProvaExecution:" + elapsedTimeProvaExecution + " milliseconds");
			}
		}
	}

	public void sendRDFtoStore(String fileName) throws IOException {
		URL url = new URL("http://localhost:8080/DMRE-TRUNK-SNAPSHOT/DMSE/SPARQL/FeedRDF");
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod("POST");
		httpConnection.setDoOutput(true);
		httpConnection.setRequestProperty("Content-Type", "application/json");


		//read file to byteArray
		byte[] dataToSend = Files.readAllBytes(Paths.get("DMRE/100_Package/"+fileName));
		//String data = "something";
		//byte[] out = data.getBytes(StandardCharsets.UTF_8);
		OutputStream stream = httpConnection.getOutputStream();
		stream.write(dataToSend);

//		System.out.println(httpConnection.getResponseCode());
//		System.out.println(httpConnection.getResponseMessage());
		httpConnection.getResponseMessage();
		httpConnection.disconnect();
		System.out.println("Sent RDF to Store");

	}
	public void removeRDFfromStore() throws IOException {
		URL url = new URL("http://localhost:8080/DMRE-TRUNK-SNAPSHOT/DMSE/SPARQL/CleanRDF");
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod("POST");
		httpConnection.setDoOutput(true);
		httpConnection.setRequestProperty("Content-Type", "application/json");
		byte[] out = "".getBytes(StandardCharsets.UTF_8);
		OutputStream stream = httpConnection.getOutputStream();
		stream.write(out);
//		System.out.println(httpConnection.getResponseCode());
//		System.out.println(httpConnection.getResponseMessage());
		httpConnection.getResponseMessage();
		httpConnection.disconnect();
		System.out.println("Removed RDF from Store");

	}
	public void executeProvaGuideline() {
		System.out.println("initiating the SPARQLAskerIncludingProva");
		SPARQLAskerIncludingProva asker = new SPARQLAskerIncludingProva();
		asker.run();
	}
	public void retrieveResult(String result) {
		this.resultToStore = result;
	}
	public void writeInformationToCSV(String fileName, String result, long elapsedTimeProvaExecution, long elapsedTime) throws IOException {
		PrintWriter writerCSV=null;
		//aligning the filename to the name.... (removing rdf_representation_ and rdf.xml)
		fileName = fileName.replaceAll("rdf_representation_", "");
		fileName = fileName.replaceAll(".rdf.xml", "");
		fileName = fileName.replaceAll("_", " ");
		//check the result, and transform to Critical / not critical
		String[] tmpResult;
		try {
			tmpResult = result.split("=");
			int count = 0;
			for(int i = 0; i<tmpResult.length;i++) {
				if(tmpResult[i].contains("ABCDECheckSPARQL")) {
					count++;
				}
			}
			if(count==1) {
				result="NOT CRITICAL";
			}else {
				result="CRITICAL";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		try{
			writerCSV = new PrintWriter(new FileOutputStream(
				    new File("/Users/gerhard/eclipse/dev/DMRE/100_Package/WorkflowRunnerResult.csv"), true));
			StringBuilder sb = new StringBuilder();
			sb.append(fileName);sb.append(",");
			sb.append(result);sb.append(",");
			sb.append(elapsedTimeProvaExecution);sb.append(",");
			sb.append(elapsedTime);sb.append("\n");
			writerCSV.write(sb.toString());
			writerCSV.flush();
			writerCSV.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			result = "CLEARED";
		}
	}
}
