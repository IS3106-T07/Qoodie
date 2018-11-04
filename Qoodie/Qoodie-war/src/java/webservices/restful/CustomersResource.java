/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package webservices.restful;

import entity.Customer;
import entity.CustomerOrder;
import error.CustomerNotFoundException;
import error.CustomerOrderAlreadyPaidException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import session.CustomerOrderSessionBeanLocal;
import session.CustomerSessionBeanLocal;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("customers")
public class CustomersResource {
    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;
    
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
    
    //3 get a customer
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long cId)  {
        try {
            Customer c = customerSessionBeanLocal.readCustomer(cId);
            return Response.status(200).entity(
                    c
            ).type(MediaType.APPLICATION_JSON).build();
        } catch (CustomerNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "customer not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
    //4
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCustomer(@PathParam("id") Long cId, Customer c) {
        c.setId(cId);
        try {
            customerSessionBeanLocal.updateCustoemr(c);
            return Response.status(204).build();
        } catch (CustomerNotFoundException e) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
    //5
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("id") Long cId) {
        try {
            Customer c = customerSessionBeanLocal.readCustomer(cId);
            customerSessionBeanLocal.deleteCustomer(c);
            return Response.status(204).build();
        } catch (CustomerNotFoundException e) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Not found")
                    .build();
            return Response.status(404).entity(exception).build();
        }
    }
    
    
    //6 create a cusotomer Order (type: in baseket)for a customer
    @POST
    @Path("/{customer_id}/orders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomerOrder(@PathParam("customer_id") Long cId, CustomerOrder o)  {
        try {
            customerOrderSessionBeanLocal.createCustomerOrder(o);
            Customer customer = customerSessionBeanLocal.readCustomer(cId);
            //add association
            o.setCustomer(customer);
            customer.getCustomerOrders().add(o);
            //update both entities
            customerOrderSessionBeanLocal.updateCustomerOrder(o);
            customerSessionBeanLocal.updateCustoemr(customer);
            return Response.status(200).entity(customer).build();
        } catch (CustomerNotFoundException ex){
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Customer not found")
                    .build();
            return Response.status(404).entity(exception).build();
        } catch (CustomerOrderTypeNotFoundException ex) {
             JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Customer  order type not found")
                    .build();
            return Response.status(404).entity(exception).build();
        } catch (CustomerOrderNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Customer order not found")
                    .build();
            return Response.status(404).entity(exception).build();
        }
    }
    
    //7 update tthe cusotmer order type to : PAID
    @GET
    @Path("/{order_id}/pay")
    public Response payOrder(@PathParam("order_id") Long oId) {
        try {
            CustomerOrder o = customerOrderSessionBeanLocal.readCustomerOrder(oId);
            customerOrderSessionBeanLocal.payCustomerOrder(o);
            return Response.status(200).entity(o).build();
        } catch (CustomerOrderNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "order not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        } catch (CustomerOrderTypeNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "order type not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        } catch (CustomerOrderAlreadyPaidException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "order already paid")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
}
