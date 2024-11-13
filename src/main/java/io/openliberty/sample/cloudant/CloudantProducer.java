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
package io.openliberty.sample.cloudant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.ibm.cloud.cloudant.v1.Cloudant;
import com.ibm.cloud.cloudant.v1.model.GetDatabaseInformationOptions;
import com.ibm.cloud.cloudant.v1.model.PutDatabaseOptions;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;

@ApplicationScoped
public class CloudantProducer {
    
    @Inject
    @ConfigProperty(name = "cloudant.apikey") 
    String apikey;
    
    @Inject
    @ConfigProperty(name = "cloudant.host", defaultValue = "localhost") 
    String host;
    
    @Inject
    @ConfigProperty(name = "cloudant.dbname") 
    String dbname;

    @Produces
    public Cloudant createCloudant() {
        IamAuthenticator authenticator = new IamAuthenticator.Builder()
                .apikey(apikey)
                .build();

        Cloudant service = new Cloudant(Cloudant.DEFAULT_SERVICE_NAME, authenticator);
        service.setServiceUrl("https://" + host);

        // Check if the database exists, if not, create it
        GetDatabaseInformationOptions dbInfoOptions = new GetDatabaseInformationOptions.Builder()
                .db(dbname)
                .build();

        try {
            service.getDatabaseInformation(dbInfoOptions).execute();
            System.out.println("connected to existing database " + dbname );
        } catch (NotFoundException e) {
            PutDatabaseOptions dbOptions = new PutDatabaseOptions.Builder()
                    .db(dbname)
                    .build();
            try{
                service.putDatabase(dbOptions).execute();
                System.out.println("Created new database " + dbname );
            }catch(Exception c){
                c.printStackTrace(System.out);
            }
            
        }

        return service;
    }
}
