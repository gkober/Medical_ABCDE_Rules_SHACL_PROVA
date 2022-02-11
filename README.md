# Medical_ABCDE_Rules_SHACL_PROVA

In this project, we implement the same medical ABCDE-approach, as it is well know to medical doctors, nurses and paramedics. The main issue is, that these persongroups tend to leave out important checks, and support helps in patient care. Furthermore, there is not yet a technical approach that implements such a medical workflow.

## Executing the PROVA-Implementation
The Prova implementation is a more integrated version, since it has some prerequisites:
* an RDF-Data-Store (at least a short-term), to submit and delete RDF graphs
* the Prova-files containing the rules
* Networking capabilities to access the RDF-Datastore

*WorkflowRunnerAll* is the starting point of the execution; it goes over all generated FHIR observations, submits each of them to the RDF-Store, runs the Prova rules against the data, waits for the completion and the outcome, and finally stores it (for potential measurements) to a csv-file.

It needs a webserver (e.g. Tomcat) for running the SPARQLService to submit, remove and ask SPARQL-queries.

## Executing the SHACL-Implementation

## Generation of FHIR-Data
The *generateObservationSamples* takes care on generating a bunch of - specific FHIR-Bundles, containing randomly generated data. 
The 
* Family Names
* Given Names (female/male)
* streetnames
* Cities

are generated out of existing lists, the observation-examples are randomly choosen, by the needs of the wanted result. (either a critical or a not-critical result). The data-generator also takes care that there is about half of the observations in critical shape, and the other half is in non-critical status. The code takes care, on filling possible values, so that they should make sense, from a medical perspective. (yes, maybe not perfectly, but for the experiments we want to run using Prova and SHACL, it is sufficient).
The Data-generator first creates FHIR-JSON-Objects (which could also be submitted to a FHIR-Store), and then they are transfered (by using HAPI-FHIR) to RDF-Representation. Furthermore, we enrich the RDF-Representation with the FHIR-Ontology (http://www.hl7.org/fhir/rdf.html and http://www.hl7.org/fhir/downloads.html), that allows then a usage of these files in Protege (https://protegewiki.stanford.edu/wiki/WebProtege). Finally, we convert the TTL-Representation of the FHIR-Bundles, also to an RDF-XML-Representation.

## 1000_samples
1000_samples contains results from the FHIR-Data-generator. in there is a generated CSV-File, which is the inputfile for the transformation to FHIR-Bundles. The ttl-files (in the ZIP-File), are the already transformed FHIR-Bundles as ttl-representation
