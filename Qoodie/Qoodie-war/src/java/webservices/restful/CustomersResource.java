/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Customer;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CustomerSessionBeanLocal;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("customers")
public class CustomersResource {
    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;

    //1 admin get all customers
    @GET
    @Produces("application/json")
    public List<Customer> getAllCustomers(){
        System.out.println("***************reading all customers************");
        return customerSessionBeanLocal.readAllCustomer();
    }
    
    //2 Create a new Customer by using the request body data. 
    //Should return empty payload if the creation is successful (204)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer c) {
        customerSessionBeanLocal.createCustomer(c);
        return Response.status(204).build();
    }
    
    //3
    
    
}
