import java.io.*;
import java.util.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;

import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;

public class executeABCDE 
{
    public static void main(String[] args) throws Exception 
    {
            //Load the ontology. It is sufficient to load the ABox.
        File folder =  new File("./1000_samples");
        File output = new File("WorkflowRunnerResult_correctResultsViaSHACL.csv");
        while(output.exists())output.delete();
        PrintStream ps = new PrintStream(output, "UTF-8");
        for(File f:folder.listFiles())
        {
            if(f.getName().indexOf(".ttl")!=f.getName().length()-4)continue;
            
            FileInputStream fis = new FileInputStream(f);
            Model ontology = JenaUtil.createMemoryModel();
            ontology.read(fis, "urn:dummy", FileUtils.langTurtle);
            fis.close();

                //SHACL inference rules
            Model rules = JenaUtil.createMemoryModel();
            FileInputStream fisRules = new FileInputStream("rulesABCDE.ttl");
            rules.read(fisRules, "urn:dummy", FileUtils.langTurtle);
            fisRules.close();
            Model inferredModel = RuleUtil.executeRules(ontology, rules, null, null);

            String value = inferredModel.listSubjects().toList().get(0).getProperty(inferredModel.getProperty("http://www.w3.org/2000/01/rdf-schema#comment")).getObject().toString();
            String patientID = inferredModel.listSubjects().toList().get(0).getProperty(inferredModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label")).getObject().toString();
            RDFNode given = ontology.listObjectsOfProperty(ontology.getProperty("http://hl7.org/fhir/HumanName.given")).toList().get(0);
            RDFNode family = ontology.listObjectsOfProperty(ontology.getProperty("http://hl7.org/fhir/HumanName.family")).toList().get(0);
            RDFNode name = ontology.listObjectsOfProperty(given.asResource(), ontology.getProperty("http://hl7.org/fhir/value")).toList().get(0);
            RDFNode surname = ontology.listObjectsOfProperty(family.asResource(), ontology.getProperty("http://hl7.org/fhir/value")).toList().get(0);


            //System.out.println(patientID);
            //System.out.println(value);
            if(value.compareToIgnoreCase("NOT CRITICAL")!=0)value="CRITICAL";
            System.out.println("File: "+f.getName()+" => "+name+" "+surname+","+value);
            ps.println(surname+" "+name+","+value);
        }
        
        ps.close();

        System.out.println("\n\nCOMPARISON:\n\n");
        Hashtable<String,String> ht = new Hashtable<String,String>();
        File compare = new File("./WorkflowRunnerResult_correctResults.csv");
        FileInputStream fis = new FileInputStream(compare);
        InputStreamReader isr = new InputStreamReader(fis, java.nio.charset.StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String buffer = "";
        while((buffer=br.readLine())!=null)
        {
            String surnamename = buffer.substring(0, buffer.indexOf(",")).trim();
            String outcome = buffer.substring(buffer.indexOf(",")+1,buffer.indexOf(",",buffer.indexOf(",")+1)).trim();
            ht.put(surnamename, outcome);
        }
        br.close();
        isr.close();
        fis.close();
        
        fis = new FileInputStream(output);
        isr = new InputStreamReader(fis, java.nio.charset.StandardCharsets.UTF_8);
        br = new BufferedReader(isr);
        buffer = "";
        while((buffer=br.readLine())!=null)
        {
            String surnamename = buffer.substring(0, buffer.indexOf(",")).trim();
            String outcome = buffer.substring(buffer.indexOf(",")+1,buffer.length()).trim();
            String value = ht.get(surnamename);
            
            if(value.compareToIgnoreCase(outcome)!=0)
            {
                System.out.println("Patient "+surnamename+" has different outputs in SHACL and Prova!");
                return;
            }
        }
        System.out.println("No differences between SHACL and Prova!");
    }
}