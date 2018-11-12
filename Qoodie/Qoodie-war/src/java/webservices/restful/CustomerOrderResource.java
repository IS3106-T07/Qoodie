/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderDish;
import error.OrderDishNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.primefaces.json.JSONArray;
import session.CustomerOrderSessionBeanLocal;
import session.CustomerSessionBeanLocal;
import webservices.restful.helper.Base64AuthenticationHeaderHelper;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("customerOrders")

public class CustomerOrderResource {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    //19 a customer gets his/her customer order with the type "IN BASKET" 
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editOrderDish(@HeaderParam("Authentication") String authHeader, OrderDish od) throws OrderDishNotFoundException {
        String email = Base64AuthenticationHeaderHelper.
                getUsernameOrErrorResponseString(authHeader);
        if (email.toLowerCase().contains("not found")) {
            return getAuthNotFoundResponse();
        }
        String password = Base64AuthenticationHeaderHelper.
                getPasswordOrErrorResponseString(authHeader);

        List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
        if (customerList.isEmpty()) //CASE: email not in database
        {
            return getUserNotFoundResponse();
        }

        Customer customer = customerList.get(0);

        if (customer.getPassword().equals(password)) { //correct credantial. perform logic
            List<CustomerOrder> customerOrders = customer.getCustomerOrders();
            List<CustomerOrder> resultCustomerOrders = new ArrayList<>();
            for (CustomerOrder co : customerOrders) {
                if (co.getCustomerOrderType().getName().contains("IN BASKET")) {
                    resultCustomerOrders.add(co);
                } 
            }
            JSONArray array = new JSONArray(resultCustomerOrders);
            return Response.status(200).entity(array)
                    .type(MediaType.APPLICATION_JSON).build();
        } else {
            return getWrongPasswordResponse();
        } //TODO: test this endpoint
    }

    private Response getAuthNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "authentication informaiton not found")
                .build();
        return Response.status(404).entity(exception)
                .type(MediaType.APPLICATION_JSON).build();
    }

    private Response getUserNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "user not found")
                .build();
        return Response.status(404).entity(exception)
                .type(MediaType.APPLICATION_JSON).build();
    }

    private Response getWrongPasswordResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "wrong passord")
                .build();
        return Response.status(401).entity(exception)
                .type(MediaType.APPLICATION_JSON).build();
    }
}
