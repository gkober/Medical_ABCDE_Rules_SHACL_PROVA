# Medical_ABCDE_Rules_SHACL_PROVA

In this project, we implement the same medical ABCDE-approach, as it is well know to medical doctors, nurses and paramedics. The main issue is, that these persongroups tend to leave out important checks, and support helps in patient care. Furthermore, there is not yet a technical approach that implements such a medical workflow.

## Executing the PROVA-Implementation
The Prova implementation is a more integrated version, since it has some prerequisites:
* an RDF-Data-Store (at least a short-term), to submit and delete RDF graphs
* the Prova-files containing the rules
* Networking capabilities to access the RDF-Datastore

*WorkflowRunnerAll* is the starting point of the execution; it goes over all generated FHIR observations, submits each of them to the RDF-Store, runs the Prova rules against the data, waits for the completion and the outcome, and finally stores it (for potential measurements) to a csv-file.

## Executing the SHACL-Implementation

## Generation of FHIR-Data
The *generateObservationSamples* takes care on generating a bunch of - specific FHIR-Bundles, containing randomly generated data. 
The 
* Family Names
* Given Names (female/male)
* streetnames
* Cities

are generated out of existing lists, the observation-examples are randomly choosen, by the needs of the wanted result. (either a critical or a not-critical result). The data-generator also takes care that there is about half of the observations in critical shape, and the other half is in non-critical status. The code takes care, on filling possible values, so that they should make sense, from a medical perspective. (yes, maybe not perfectly, but for the experiments we want to run using Prova and SHACL, it is sufficient).
