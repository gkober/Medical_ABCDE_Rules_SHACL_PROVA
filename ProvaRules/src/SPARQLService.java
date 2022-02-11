package com.spirit.DMRE;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.BooleanQueryResultHandler;
import org.eclipse.rdf4j.query.Query.QueryType;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLBooleanJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.MediaType;

import com.spirit.DMRE.RDF.RDFRepository;
import com.spirit.DMRE.Utils.*;

/**
 *
 * @author gerhard
 * SPARQL-Sample on FHIR-Resources....
 *   prefix fhir:<http://hl7.org/fhir/>
 *   select  ?code ?value   where {
 *     ?x fhir:Bundle.entry.resource/fhir:Observation.code/fhir:CodeableConcept.coding/fhir:Coding.code/fhir:value ?code.
 *     ?value ^fhir:value/^fhir:Quantity.value/^fhir:Observation.valueQuantity/^fhir:Bundle.entry.resource ?x.
 *     FILTER(?code='2339-0')
 *   }
 *
 *
 *   --- alternative ---
 *   prefix fhir:<http://hl7.org/fhir/>
 *   select  ?code ?value   where {
 *   	?x fhir:Bundle.entry.resource/fhir:Observation.code/fhir:CodeableConcept.coding/fhir:Coding.code/fhir:value ?code.
 *      ?value ^fhir:value/^fhir:Quantity.value/^fhir:Observation.valueQuantity/^fhir:Bundle.entry.resource ?x.
 *      FILTER(?code='2339-0' && ?value>=60 && ?value<100).
 *   }
 *
 */
@Path("/SPARQL")
public class SPARQLService{
	private static Logger logger = Logger.getLogger("com.spirit.DMRE.SPARQLService");
	@Context
    private UriInfo context;
	@Context
	private Request request;

	private static RDFRepository cacheRDFRepo = new RDFRepository();
	/**
	 * The DistributedMedicalServiceEngine is the core server, which allows to start different services, as needed in the DMRE Distributed environment
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/inference")
	public Response SPARQLQuery(String jsonrequest) {
		logger.info("[SPARQLService] now in POST-Method for a SPARQL-Query");
		logger.fine("[SPARQLService] JSON-Request: " + jsonrequest);
		String queryString = new StringToJsonConverter().getSPARQLQueryFromJson(jsonrequest, "sparqlquery");
		String rdf = new StringToJsonConverter().getSPARQLQueryFromJson(jsonrequest, "rdf");
		logger.fine("[SPARQLService] RDF to use: " + rdf);
		//loading configuration & initializing variables
		ConfigurationReader cfg = new ConfigurationReader();
		OutputStream out1 = null;
		String filename = cfg.getcacheSparqlResultdirectory()+ "_cacheSparqlResult_" +UUID.randomUUID().toString()+".tmp";
		RDFRepository temporaryRepository = new RDFRepository();
		ParsedQuery parsedQuery = QueryParserUtil.parseQuery(QueryLanguage.SPARQL, queryString, null);

		try {
			temporaryRepository.createTemporaryRDFRepo(rdf);
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//parsing the query to decide the type of query
		//System.out.println(QueryParserUtil.removeSPARQLQueryProlog(queryString).toUpperCase());
		//create an if for ASK-Queries ....
		if(QueryParserUtil.removeSPARQLQueryProlog(queryString).toUpperCase().startsWith("ASK")) {
			System.out.println("this is the ask-query");
			//QueryType type =
			BooleanQuery askQuery = temporaryRepository.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL,queryString);
			try {
				out1 = new FileOutputStream(filename);
				BooleanQueryResultHandler writer = new SPARQLBooleanJSONWriter(out1);
				Boolean queryResult = askQuery.evaluate();
				writer.handleBoolean(queryResult);
				System.out.println("QueryResult for ASK-query: " + queryResult);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		// following is an option for redoing the query and the sparql -to json ....

		if(QueryParserUtil.removeSPARQLQueryProlog(queryString).toUpperCase().startsWith("SELECT")) {
			System.out.println("this is the select-query");
			TupleQuery query1 = temporaryRepository.getConnection().prepareTupleQuery(QueryLanguage.SPARQL,queryString);
			logger.fine("[SPARQLService] TemporaryFileName is: " + filename);
			System.out.println("SPARQL-Service: " + filename);
			try {
				out1 = new FileOutputStream(filename);
				TupleQueryResultHandler writer = new SPARQLResultsJSONWriter(out1);
				query1.evaluate(writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Read caching-file and stream to response
		StreamingOutput fileStream =  new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					java.nio.file.Path path = Paths.get(filename);
					byte[] data = Files.readAllBytes(path);
					output.write(data);
					output.flush();
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					System.out.println("[SPARQLService] deletecacheSparqlResultFile: " + cfg.getdeletecacheSparqlResultFile());
					logger.fine("[SPARQLService] deletecacheSparqlResultFile: " + cfg.getdeletecacheSparqlResultFile());
					if(cfg.getdeletecacheSparqlResultFile()) {
						logger.fine("[SPARQLService] Trying to remove Chache-File from Disk");
						try {
							Files.deleteIfExists(Paths.get(filename));
						}catch(Exception e) {
							e.printStackTrace();
						}
						logger.info("[SPARQLService] Removed " + filename + " from disk");
					}
					else {
						logger.info("Configuration deletecacheSparqlResultFile set to false - keeping file");
						logger.info("filename: " + filename);
					}

				}
			}
		};

		return Response.ok(fileStream, new MediaType("application","sparql-results+json")).build();
	}
	// @POST ... not yet sure if needed only for queries...
	@GET
	//@Consumes(MediaType.APPLICATION_JSON)
	@Path("/SPARQL")
	public Response query(@QueryParam("query") String requestString) {
		System.out.println(this.cacheRDFRepo.getSize());
		System.out.println(requestString);
		ConfigurationReader cfg = new ConfigurationReader();
		String filename = cfg.getcacheSparqlResultdirectory()+ "_cacheSparqlResult_" +UUID.randomUUID().toString()+".tmp";
		OutputStream out1 = null;
		if(QueryParserUtil.removeSPARQLQueryProlog(requestString).toUpperCase().startsWith("SELECT")) {
			System.out.println("this is the select-query");

			TupleQuery query1 = this.cacheRDFRepo.getConnection().prepareTupleQuery(QueryLanguage.SPARQL,requestString);
			try {
				System.out.println("trying to write the result to disk...");
				out1 = new FileOutputStream(filename);
				TupleQueryResultHandler writer = new SPARQLResultsJSONWriter(out1);
				query1.evaluate(writer);
				out1.flush();
				out1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(QueryParserUtil.removeSPARQLQueryProlog(requestString).toUpperCase().startsWith("ASK")) {
			System.out.println("this is the ask-query");
			//QueryType type =
			BooleanQuery askQuery = this.cacheRDFRepo.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL,requestString);
			try {
				out1 = new FileOutputStream(filename);
				BooleanQueryResultHandler writer = new SPARQLBooleanJSONWriter(out1);
				Boolean queryResult = askQuery.evaluate();
				writer.handleBoolean(queryResult);
				System.out.println("QueryResult for ASK-query: " + queryResult);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Read caching-file and stream to response
		StreamingOutput fileStream =  new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					java.nio.file.Path path = Paths.get(filename);
					System.out.println(filename);
					byte[] data = Files.readAllBytes(path);
					output.write(data);
					output.flush();
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					System.out.println("[SPARQLService] deletecacheSparqlResultFile: " + cfg.getdeletecacheSparqlResultFile());
					if(cfg.getdeletecacheSparqlResultFile()) {
						logger.fine("[SPARQLService] Trying to remove Chache-File from Disk");
						try {
							Files.deleteIfExists(Paths.get(filename));
						}catch(Exception e) {
							e.printStackTrace();
						}
						logger.info("[SPARQLService] Removed " + filename + " from disk");
					}
					else {
						logger.info("Configuration deletecacheSparqlResultFile set to false - keeping file");
						logger.info("filename: " + filename);
					}

				}
			}
		};

		return Response.ok(fileStream, new MediaType("application","sparql-results+json")).build();
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/FeedRDF")
	public Response feedRDF(String requestString) {
		try {
			this.cacheRDFRepo.createCacheRDFRepo(requestString);
			return Response.ok().build();
		} catch (RDFParseException | RepositoryException | IOException e) {
			e.printStackTrace();
			return Response.ok(e.getMessage()).build();
		}

	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/CleanRDF")
	public Response CleanRDF(String requestString) {
		try {
			this.cacheRDFRepo.cleanTemporaryRDFRepo();
			return Response.ok().build();
		} catch (RDFParseException | RepositoryException e) {
			e.printStackTrace();
			return Response.ok(e.getMessage()).build();
		}

	}

}
