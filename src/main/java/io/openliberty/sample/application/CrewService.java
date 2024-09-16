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

@Path("/crew")
@ApplicationScoped
public class CrewService {

	//@Inject
	//TODO Inject a cloudant instance here

	@Inject
	Validator validator;

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
		return "";
	}

	@DELETE
	@Path("/{id}")
	public String remove(@PathParam("id") String id) {
        //TODO Remove CrewMember from cloudant database
		return "";
	}



	@GET
	public String retrieve() {
		StringWriter sb = new StringWriter();
		try {
			sb.append("[");
            //TODO Retrieve CrewMembers from cloudant and return as a Json Array
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return sb.toString();
	}
}