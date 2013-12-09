RQWST0 - SPARQL (.rq) with Stringtemplate (.stg)
================================================

The purpose of the rqwst0 java program is to take the result of one or more SPARQL query (.rq) and process it is using
according to templates made using StringTemplate  (.st or .stg extension).

This makes it possible to use the results from a SPARQL query and then generate code reflecting the query.

The work is motivated by the existence of CDISC Standards in RDF. SPARQL results can be transformed using other means, for example XSLT. 

Combining SPARQL and the template engine StringTemplate makes it possible to leverage the expressiveness of StringTemplate as code generation engine to use the CDISC standards in RDF to generate programs and overviews. 

Thanks to
=========

Apache Jena and Jena Fuseki - provides the real work - http://jena.apache.org 

CDISC Standards in RDF - this https://github.com/phuse-org/rdf.cdisc.org

StringTemplate -  provides the real work -  http://stringtemplate.org

StringTemplate Standalone Tool (STST): http://hardlikesoftware.com/weblog/stst/, http://www.antlr.org/wiki/display/ST/STST+-+StringTemplate+Standalone+Tool
STST Takes data in JSON and processing it using StringTemplate, and served as inspiration for RQWST0. 
 
Invocation
==========

java -classpath <provide path to Jena and StringTemplate jars> --query ( <SPARQL query .rq> (<optional StringTemplate attribute name>) )+ --stringtemplate <StringTemplate group template .stg> <StringTemplate template to invoke> 

See examples below


Parameters
==========

--query ( <SPARQL query .rq> (<optional StringTemplate attribute name>) )+
RQWST0 processes each SPARQL query, stores the result as a StringTemplate aggregated attribute.
If the  <optional StringTemplate attribute name> is not present, then filename part (excluding path and extension) from the SPARQL query file is used. 

--stringtemplate <StringTemplate group template .stg> <StringTemplate template to invoke> 
Load the StringTemplate group template and invokdes the given StringTemplate template.

Files
=====


RQWST0 java program
----------

rqwst0.java: the program


SPARQL Examples (.rq)
----------
sdtm-ae.rq:# CDISC SDTM domain DM fields
sdtm-dm.rq:# CDISC SDTM domain DM fields
sdtm-terminology-codelist-lab.rq:# CDISC terminology codelist for lab parameters
sdtm-terminology-codelist-sex.rq:# CDISC terminology codelist for Sex

The queries have hardcoded the SPARQL Service corresponding to the local Jena Fuseki endpoint.
This works for the setup described here. Remove the reference to the service if it does not fit with your setup.

StringTemplate Example (.stg)
----------
rqwst0-example.stg: several templates

Configuration file for Jena Fuseki using CDISC Standard in RDF (see below)
----------
config-cdisc-ttl-direct.ttl

Pre-requisites
==============

Apache Jena - download from http://jena.apache.org/download/index.html
Download apache-jena and jena-fuseki 

Stringtemplate 4 - download from http://stringtemplate.org/download.html, use Java StringTemplate 4.0.7 binary jar (or later)

Note: on Windows I unpack the distributions to c:\opt - see below.

Representation of CDISC Standards in RDF: https://github.com/phuse-org/rdf.cdisc.org
Suggest storing the standards - the directory rdf.cdisc.org and subdirectories - in the same directory as Jena Fuseki.
Then use the Jena Fuseki configuration file available here (config-cdisc-ttl-direct.ttl) to set-up the SPARQL endpoint Jena Fuseki.

Java installation with javac.

Usage example
=====

Start Jena Fuseki SPARQL endpoint
---


Open command window
cd c:\opt\jena-fuseki-1.0.0
c:/opt/jena-fuseki-1.0.0/fuseki-server.bat --config=config-cdisc-ttl-direct.ttl

To stop the fuseki-server: pres ctrl-C
Suggestion: Test the server by opening http://localhost:3030/control-panel.tpl - the page should show "Dataset: /cdisc" - press Select - and you are ready to enter a SPARQL query.
The contents of the SPARQL examples here (*.rq) can be copy-pasted into the text field.  

Compile RQWST0 from prompt
---

REM Set path to java
PATH=c:\Program Files\Java\jdk1.7.0_25\bin;%PATH%

cd <directory with RQWST0>
javac -classpath \opt\apache-jena-2.11.0\lib\*;\opt\stringtemplate4\* rqwst0.java

Examples
---

Execute program with parameters

java -classpath .;\opt\apache-jena-2.11.0\lib\*;\opt\stringtemplate4\* rqwst0 --query sdtm-ae.rq ae sdtm-dm.rq dm --stringtemplate rqwst0-example.stg showrqs

java -classpath .;\opt\apache-jena-2.11.0\lib\*;\opt\stringtemplate4\* rqwst0 --query sdtm-terminology-codelist-lab.rq cts --stringtemplate rqwst0-example.stg showcts

java -classpath .;\opt\apache-jena-2.11.0\lib\*;\opt\stringtemplate4\* rqwst0 --query sdtm-terminology-codelist-sex.rq cts --stringtemplate rqwst0-example.stg showcts

java -classpath .;\opt\apache-jena-2.11.0\lib\*;\opt\stringtemplate4\* rqwst0 --query sdtm-terminology-codelist-sex.rq cts --stringtemplate rqwst0-example.stg formatcts

java -classpath .;\opt\apache-jena-2.11.0\lib\*;\opt\stringtemplate4\* rqwst0 --query sdtm-terminology-codelist-lab.rq cts --stringtemplate rqwst0-example.stg formatcts

Note the output can be redirected to a file.

To do
----

Jave program is a mess - rewrite - current version is just statements put together (hence the name with 0 at the end)

Better check of arguments and warning messages

Make more SPARQL querie

Make more StringTemplate scripts to generate SAS and R code

Consider if graphs may be mapped into StringTemplate aggegated attributes

Add option providing filename to store output
