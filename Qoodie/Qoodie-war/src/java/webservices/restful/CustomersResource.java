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
import error.OrderDishNotFoundException;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CustomerOrderSessionBeanLocal;
import session.CustomerSessionBeanLocal;
import webservices.restful.helper.Base64AuthenticationHeaderHelper;
import webservices.restful.helper.Flattener;
import webservices.restful.helper.PATCH;

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

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

    //1 admin get all customers
    @GET
    @Produces("application/json")
    public List<Customer> getAllCustomers() {
        System.out.println("***************reading all customers************");
        List<Customer> customers = customerSessionBeanLocal.readAllCustomer();
        for (Customer c : customers) {
            c = Flattener.flatten(c);
        }
        return customers;
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
    public Response getCustomer(@PathParam("id") Long cId) {
        try {
            Customer c = customerSessionBeanLocal.readCustomer(cId);
            return Response.status(200).entity(
                    Flattener.flatten(c)
            ).type(MediaType.APPLICATION_JSON).build();
        } catch (CustomerNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "customer not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

    //4 edit a customer. 
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCustomer(@HeaderParam("Authorization") String authHeader, Customer newC) {

        String email = Base64AuthenticationHeaderHelper.
                getUsernameOrErrorResponseString(authHeader);
        if (email.toLowerCase().contains("not found")) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "authentication informaiton not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
        String password = Base64AuthenticationHeaderHelper.
                getPasswordOrErrorResponseString(authHeader);
        
        List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
        if (customerList.isEmpty()) //CASE: email not in database
        {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "user not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
        
        Customer c = customerList.get(0);
        newC.setId(c.getId());
        if (c.getPassword().equals(password)) { //correct credantial. can edit
            try {
                customerSessionBeanLocal.updateCustoemr(newC);
                return Response.status(204).build();
            } catch (CustomerNotFoundException e) {
                JsonObject exception = Json.createObjectBuilder()
                        .add("error", "Not found")
                        .build();
                return Response.status(404).entity(exception)
                        .type(MediaType.APPLICATION_JSON).build();
            }
        } else {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "wrong passord")
                    .build();
            return Response.status(401).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

    // 5 
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("id") Long cId
    ) {
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
    public Response addCustomerOrder(@PathParam("customer_id") Long cId, CustomerOrder o
    ) {
        try {
            customerOrderSessionBeanLocal.createCustomerOrder(o);
            Customer customer = customerSessionBeanLocal.readCustomer(cId);
            //add association
            o.setCustomer(customer);
            customer.getCustomerOrders().add(o);
            //update both entities
            customerOrderSessionBeanLocal.updateCustomerOrder(o);
            customerSessionBeanLocal.updateCustoemr(customer);
            return Response.status(200).entity(Flattener.flatten(customer)).build();
        } catch (CustomerNotFoundException ex) {
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
        } catch (OrderDishNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Order Dish not found")
                    .build();
            return Response.status(404).entity(exception).build();
        }
    }

    /*
    //7 update tthe cusotmer order type to : PAID
    @GET
    @Path("/{order_id}/pay")
    public Response payOrder(@PathParam("order_id") Long oId
    ) {
        try {
            CustomerOrder o = customerOrderSessionBeanLocal.readCustomerOrder(oId);
            customerOrderSessionBeanLocal.payCustomerOrder(o);
            return Response.status(200).entity(Flattener.flatten(o)).build();
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
    */

    //12
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("Authorization") String authHeader
    ) {
        if (authHeader == null || authHeader.length() == 0) { // CASE: no auth token in the header
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "authentication informaiton not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        } else {
            String authToken = authHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String decodedString = new String(Base64.getDecoder().decode(authToken));
            StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
            String email = tokenizer.nextToken();
            String password = tokenizer.nextToken();

            List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
            if (customerList.isEmpty()) //CASE: wrong email or password
            {
                JsonObject exception = Json.createObjectBuilder()
                        .add("message", "user not found")
                        .build();
                return Response.status(404).entity(exception)
                        .type(MediaType.APPLICATION_JSON).build();
            } else {
                Customer customer = customerList.get(0);
                if (customer.getPassword().equals(password)) {
                    return Response.status(200).entity(Flattener.flatten(customer))
                            .type(MediaType.APPLICATION_JSON).build();
                } else {
                    JsonObject exception = Json.createObjectBuilder()
                            .add("message", "wrong passord")
                            .build();
                    return Response.status(401).entity(exception)
                            .type(MediaType.APPLICATION_JSON).build();
                }
            }
        }
    }
}
