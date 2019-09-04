# librAIry NLP toolkit

`nlp` service provides an efficient and easy way to analyze large amounts of texts through standard HTTP and TCP APIs.

## Features
Built on top of several NLP open-source tools it offers: 
- Part-of-Speech Tagger (and filter)
- Lemmatizer
- N-Grams Identifier
- Wikipedia Relations
- Wordnet Synsets 

And all this by means of json-based queries via HTTP or TCP request deployed with [Docker](https://docs.docker.com) containers.


## Quick Start

### Run locally
1. Install [Docker](https://docs.docker.com/install/)
1. Run the service by:
   ````shell script
   $ docker run --rm -p 7777:7777 -e "REST_PATH=/nlp" librairy/nlp:latest
   ````
1. Once started, the service should be available at: [http://localhost:7777/nlp](http://localhost:7777/nlp)


### Run in distributed mode
Create a [Swarm](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/) and configure as services as you need.


## Configuration
It can be tuned using the following environment variables when defining the [Docker container](https://docs.docker.com/engine/reference/run/#env-environment-variables):

| Variable  |  Description |
|---|---|
| `REST_PATH`  |Service Namespace.  (`/` by default)   |
| `REST_PORT`  |HTTP  listening port  (`7777` by default)  |
| `NLP_AVRO_PORT`  |TCP  listening port (`65111` by default)   |
| `SPOTLIGHT_ENDPOINT`   | [DBpedia Spotlight](https://www.dbpedia-spotlight.org) url (*required to discover Wikipedia references*)|
| `SPOTLIGHT_THRESHOLD`   | Minimum score (*required to discover Wikipedia references*)|


## Services
All services can include lemmatizer actions, part-of-speech tagging and even n-grams identifications.

Given a text, it: 
- `/annotations` : adds grammatical info.
- `/groups`: creates a bag-of-words.
- `/tokens`: filters words from the request.


## DBpedia Spotlight
The service uses [DBpedia Spotlight](https://www.dbpedia-spotlight.org) to identify Wikipedia resources referenced in the text.

When you enable the option `references` in the requests to the service, it is required to have made the deployment of the corresponding module:

1. Install [Docker-Compose](https://docs.docker.com/compose/install/)
1. Create and move into a directory named `nlp/`
1. Create a file named `docker-compose.yml` with the following content:
   ````yaml
   version: '2'
   services:
     dbpedia-en-spotlight:
       image: dbpedia/spotlight-english:latest
       command: java -Dfile.encoding=UTF-8 -Xmx15G -Dthreads.max=15 -Dthreads.core=15 -jar /opt/spotlight/dbpedia-spotlight-nightly-build.jar /opt/spotlight/en  http://0.0.0.0:80/rest
       restart: always
     nlp:
       image: librairy/nlp:latest
       restart: always
       ports:
        - "7777:7777"
       environment:
         - REST_PATH=/nlp
         - JAVA_OPTS=-Xmx32768m
   ````
1. Make sure you have added the DBpedia Spotlight modules for the languages you are going to use. 
1. Check that you have the necessary resources (e.g. memory, cpu, disk...), as these modules are very demanding.  
1. Run the service by: 
   ````
   $ docker-compose up
   ````
1. Check that the following traces appear (*depending on the environment it may take a few minutes*)
   ````log
   nlp_1 | [main] INFO  e.u.o.l.n.Application - Started Application in 16.847 seconds (JVM running for 18.964)
   ...
   dbpedia-en-spotlight_1  | Server started in / listening on http://0.0.0.0:80/rest
   ````   
1. Once started, the service should be available at: [http://localhost:7777/nlp](http://localhost:7777/nlp)

- The above commands run two services: DBpedia Spotlight and librAIry NLP, and uses the settings specified within `docker-compose.yml`.
- The DBpedia service has a lazy start. This means that first requests will be slower until all resources are initialized.

## Reference

You can use the following to cite the service:

```
@inproceedings{Badenes-Olmedo:2017:DTM:3103010.3121040,
 author = {Badenes-Olmedo, Carlos and Redondo-Garcia, Jos{\'e} Luis and Corcho, Oscar},
 title = {Distributing Text Mining Tasks with librAIry},
 booktitle = {Proceedings of the 2017 ACM Symposium on Document Engineering},
 series = {DocEng '17},
 year = {2017},
 isbn = {978-1-4503-4689-4},
 pages = {63--66},
 numpages = {4},
 url = {http://doi.acm.org/10.1145/3103010.3121040},
 doi = {10.1145/3103010.3121040},
 acmid = {3121040},
 publisher = {ACM},
 keywords = {data integration, large-scale text analysis, nlp, scholarly data, text mining},
}

```

## Contact
This repository is maintained by [Carlos Badenes-Olmedo](mailto:cbadenes@gmail.com). Please send me an e-mail or open a GitHub issue if you have questions.
