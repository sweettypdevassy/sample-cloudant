![](https://github.com/OpenLiberty/open-liberty/blob/master/logos/logo_horizontal_light_navy.png)
# Cloudant Sample
This sample shows how to store data with Cloudant using CDI and MicroProfile Config, as well as data validation with Jakarta Bean Validation.
## Environment Set Up
To run this sample, first [download](https://github.com/OpenLiberty/sample-cloudant/archive/main.zip) or clone this repo - to clone:
```
git clone git@github.com:OpenLiberty/sample-cloudant.git
```
### Setup CouchDb
The Cloudant client is also compatible with CouchDb, which is needed for running the sample locally. If you have Docker installed, you can use the following:
```
docker run -d --name liberty_cloudant -p 5984:5984 -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=password couchdb:3.4.2
```
## Running the Sample
From inside the sample-cloudant directory, build and start the application in Open Liberty with the following command:
```
./mvnw liberty:dev
```
Once the server has started, the application is available at http://localhost:9080
### Try it out
Give the sample a try by registering a crew member. Enter a name (a String), an ID Number (an Integer), and select a Rank from the menu, then click 'Register Crew Member'.
Two more boxes will appear, one with your crew members (which you can click to remove) and one showing how your data looks in Cloudant.
### Stop CouchDb
```
docker stop liberty_cloudant
```
### How it works
This application uses a CDI producer ([CloudantProducer.java](https://github.com/OpenLiberty/sample-cloudant/blob/main/src/main/java/io/openliberty/sample/cloudant/CloudantProducer.java)) to inject a Cloudant client. It provides access to the database in a RESTful manner in [CrewService.java](https://github.com/OpenLiberty/sample-cloudant/blob/main/src/main/java/io/openliberty/sample/application/CrewService.java) using the `/db/crew` endpoint.

Calling `POST /{id}` on the endpoint uses [Jakarta Validation](https://openliberty.io/guides/bean-validation.html) to validate the data received from the front end. [CrewMember.java](https://github.com/OpenLiberty/sample-cloudant/blob/main/src/main/java/io/openliberty/sample/application/CrewMember.java) shows the constraints as well as the messages we return to the user if those constraints aren't met.
```java
@NotEmpty(message = "All crew members must have a name!")
private String name;
@Pattern(regexp = "(Captain|Officer|Engineer)",  message = "Crew member must be one of the listed ranks!")
private String rank;
@Pattern(regexp = "^\\d+$", message = "ID Number must be a non-negative integer!")
private String crewID;
```
After validation, we use the injected CouchDatabase to insert a new document with the crew member's information:
```java
Document newCrewMember = new Document();
             newCrewMember.put("Name",crewMember.getName());
             newCrewMember.put("Rank",crewMember.getRank());
             newCrewMember.put("CrewID",crewMember.getCrewID());
             PostDocumentOptions createDocumentOptions =
                    new PostDocumentOptions.Builder()
                        .db(dbname)
                        .document(newCrewMember)
                        .build();
             DocumentResult createDocumentResponse = client
                    .postDocument(createDocumentOptions)
                    .execute()
                    .getResult();
```
Calling `DELETE /{id}` on the endpoint deletes a document corresponding to the path parameter {id}
```java
DocumentResult deleteDocumentResponse = client
                .deleteDocument(deleteDocumentOptions)
                .execute()
                .getResult();
```
Calling `GET` on the endpoint retrieves the data and does some formatting for the front end.
```java
 AllDocsResult response = client.postAllDocs(docsOptions).execute().getResult();
 for (DocsResultRow d : response.getRows()) {
                sb.append(d.getDoc().toString());
            }
```