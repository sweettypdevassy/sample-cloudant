![](https://github.com/OpenLiberty/open-liberty/blob/master/logos/logo_horizontal_light_navy.png)

# Cloudant Sample
This sample shows how to store data with Cloudant using CDI and MicroProfile Config, as well as data validation with Jakarta Bean Validation.

## Environment Set Up
To run this sample, first [download](https://github.com/OpenLiberty/sample-cloudant/archive/main.zip) or clone this repo - to clone:
```
git clone git@github.com:OpenLiberty/sample-cloudant.git
```

### Setup Cloudant / CouchDb

<!-- TODO explain setting up cloudant or couchdb -->

## Running the Sample
From inside the sample-cloudant directory, build and start the application in Open Liberty with the following command:
```
./mvnw liberty:dev
```

Once the server has started, the application is available at http://localhost:9080

### Try it out
Give the sample a try by registering a crew member. Enter a name (a String), an ID Number (an Integer), and select a Rank from the menu, then click 'Register Crew Member'.

Two more boxes will appear, one with your crew members (which you can click to remove) and one showing how your data looks in Cloudant.

### Stop Cloudant / CouchDb

<!-- TODO instructions for stopping cloudant -->

### How it works

<!-- TODO overview of how the app works -->

Calling `POST /{id}` on the endpoint uses [Jakarta Validation](https://openliberty.io/guides/bean-validation.html) to validate the data we receive from the front end. [CrewMember.java](https://github.com/OpenLiberty/sample-cloudant/tree/master/src/main/java/io/openliberty/sample/application/CrewMember.java) shows the constraints as well as the messages we return to the user if those constraints aren't met.
```java
@NotEmpty(message = "All crew members must have a name!")
private String name;

@Pattern(regexp = "(Captain|Officer|Engineer)",  message = "Crew member must be one of the listed ranks!")
private String rank;

@Pattern(regexp = "^\\d+$", message = "ID Number must be a non-negative integer!")
private String crewID; 
```

<!-- TODO explain what happens after validation in POST -->

<!-- TODO explain DELETE endpoint -->

<!-- TODO explain GET endpoint -->



