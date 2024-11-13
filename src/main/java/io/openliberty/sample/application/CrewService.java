/*******************************************************************************
* Copyright (c) 2024 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package io.openliberty.sample.application;

import java.util.Set;

import java.io.StringWriter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.ibm.cloud.cloudant.v1.Cloudant;
import com.ibm.cloud.cloudant.v1.model.AllDocsResult;
import com.ibm.cloud.cloudant.v1.model.DeleteDocumentOptions;
import com.ibm.cloud.cloudant.v1.model.DocsResultRow;
import com.ibm.cloud.cloudant.v1.model.GetDocumentOptions;
import com.ibm.cloud.cloudant.v1.model.PostAllDocsOptions;
import com.ibm.cloud.cloudant.v1.model.PostDocumentOptions;
import com.ibm.cloud.cloudant.v1.model.Document;
import com.ibm.cloud.cloudant.v1.model.DocumentResult;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;

@Path("/crew")
@ApplicationScoped
public class CrewService {

	//@Inject
	//TODO Inject a cloudant instance here
	@Inject
	Cloudant client;

	@Inject
	Validator validator;

	@Inject
    @ConfigProperty(name = "cloudant.dbname") 
    String dbname;


	@POST
	@Path("/{id}") 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON) 
	public String add(CrewMember crewMember) {
		
		Set<ConstraintViolation<CrewMember>> violations = validator.validate(crewMember);
		if(violations.size() > 0) {
			JsonArrayBuilder messages = Json.createArrayBuilder();
			for (ConstraintViolation<CrewMember> v : violations) { 			
				messages.add(v.getMessage());
			}
			return messages.build().toString();
		}

        //TODO Insert CrewMember into cloudant database
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
		return "";
	}

	@DELETE
	@Path("/{id}")
	public String remove(@PathParam("id") String id) {
        //TODO Remove CrewMember from cloudant database
		// Set the options to get the document out of the database if it exists
        GetDocumentOptions documentInfoOptions =
            new GetDocumentOptions.Builder()
                .db(dbname)
                .docId(id)
                .build();

        try {
            // Try to get the document if it previously existed in the database
            Document document = client
                .getDocument(documentInfoOptions)
                .execute()
                .getResult();

            // Delete the document from the database
            DeleteDocumentOptions deleteDocumentOptions =
                    new DeleteDocumentOptions.Builder()
                        .db(dbname)
                        .docId(id)   
                        .rev(document.getRev())
                        .build();
            DocumentResult deleteDocumentResponse = client
                .deleteDocument(deleteDocumentOptions)
                .execute()
                .getResult();
            if (deleteDocumentResponse.isOk()) {
                System.out.println("You have deleted the document.");
            }

        } catch (NotFoundException nfe) {
            System.out.println("Cannot delete document because either database or the document was not found.");
        }
		return "";
	}



	@GET
	public String retrieve() {
		StringWriter sb = new StringWriter();
		PostAllDocsOptions docsOptions = new
				  PostAllDocsOptions.Builder() .db(dbname) .includeDocs(true) .limit(10)
				  .build();
	  
		  AllDocsResult response = client.postAllDocs(docsOptions).execute().getResult(); 
		  
		  sb.append("[");
			boolean first = true;
			for (DocsResultRow d : response.getRows()) {
				if (!first) sb.append(",");
				else first = false;
				sb.append(d.getDoc().toString());
			}
			sb.append("]");
		return sb.toString();
	}
}