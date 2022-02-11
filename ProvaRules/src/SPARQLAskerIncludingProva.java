package com.spirit.DMRE.DataGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.provarules.service.EPService;
import org.provarules.service.ProvaService;
import org.provarules.service.impl.ProvaServiceImpl;


/**
 * @author gerhard
 *
 */

public class SPARQLAskerIncludingProva implements EPService {

	static private ProvaService service;
	static final String kAgent = "prova";
    static final String kPort = null;
    int errorCounterSend = 0;
    List<Object> returnList = new ArrayList<Object>();

    final String receiver_rulebase = "rules/ABCDE_SPARQL.prova";

	public SPARQLAskerIncludingProva() {
		service = new ProvaServiceImpl();
        service.init();
        // Register the runner as a callback EPService
        service.register("FHIR", this);
	}
	void run() {
        String receiver = service.instance("receiver", "");
        Map<String, String> payload = new HashMap<String, String>();
          payload.put("patientID", "1234566");
        service.consult(receiver, receiver_rulebase, "receiver1");
        service.send("xid", "receiver", "runner", "inform", payload, this);
        try {
        	synchronized (this) {
        		wait(500);
			}
        }catch(Exception e) {
        }
        service.destroy();
	}


	public static void main(String[] args) {
		System.out.println("initiating the SPARQLAskerIncludingProva");
		SPARQLAskerIncludingProva asker = new SPARQLAskerIncludingProva();
		asker.run();
	}


	@Override
	public void send(String xid, String dest, String agent, String verb, Object payload, EPService callback) {
		System.out.println("now in send-method...");
		System.out.println("Received "+verb+" from "+agent+" :"+payload + " on Destination: " + dest + " XID: " + xid);
		System.out.println("in the receivingMode.....");
		errorCounterSend++;
		returnList.add(payload);
		System.out.println(returnList.toString());
		System.out.println(errorCounterSend);
		WorkflowRunnerAll wf = new WorkflowRunnerAll();
		wf.retrieveResult(returnList.toString());
	}

}
