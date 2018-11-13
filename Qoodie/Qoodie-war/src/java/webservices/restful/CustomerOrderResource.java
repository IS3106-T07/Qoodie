/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderDish;
import error.CustomerOrderNotFoundException;
import error.OrderDishNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
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
@Path("customerOrders")

public class CustomerOrderResource {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    //20 a customer gets his/her customer order with the type "IN BASKET" 
    @GET
    @Path("/cart")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editOrderDish(@HeaderParam("Authorization") String authHeader, OrderDish od) throws OrderDishNotFoundException {
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
                    co = Flattener.flatten(co);
                    co.setCustomer(null);
                    resultCustomerOrders.add(co);
                }
            }
            System.out.println("*** cart size = " + resultCustomerOrders.size());

            GenericEntity<List<CustomerOrder>> resultCustomerOrdersGeneric
                    = new GenericEntity<List<CustomerOrder>>(resultCustomerOrders) {
                    };

            return Response.status(200).entity(resultCustomerOrdersGeneric)
                    .type(MediaType.APPLICATION_JSON).build();
        } else {
            return getWrongPasswordResponse();
        } //TODO: test this endpoint
    }

    // 21 a customer edits his/her customer order(this replaces endpoint 7)
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{order_id}")
    public Response updateCustomerOrder(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("order_id") String coId,
            CustomerOrder customerOrder) throws CustomerOrderNotFoundException {
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

        if (customer.getPassword().equals(password)) {
            try {
                CustomerOrder co = customerOrderSessionBeanLocal.readCustomerOrder(Long.valueOf(coId));

                if (!co.getCustomer().getId().equals(customer.getId())) { // check if this order belongs to the auth customer
                    return getNoPermissionResponse();
                }
                //correct credantial. perform logic
                customerOrder.setId(Long.valueOf(coId));
                customerOrderSessionBeanLocal.updateCustomerOrderNonNullFields(customerOrder);
                return Response.status(204).build();

            } catch (CustomerOrderNotFoundException e) {
                return getNotFoundResponse();
            }
        } else {
            return getWrongPasswordResponse();//TODO: test this endpoint
        }
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

    private Response getNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "not found")
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

    private Response getNoPermissionResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "no permission")
                .build();
        return Response.status(403).entity(exception)
                .type(MediaType.APPLICATION_JSON).build();
    }
}
