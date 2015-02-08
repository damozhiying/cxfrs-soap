package com.prash.camel.cxf.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.prash.sample.model.Customer;
@Path("/customerservice/")
public class CustomerService {
 
    @GET
    @Path("/customers/{id}/")
    public Customer getCustomer(@PathParam("id") String id) {
		return null;
	}
 

}