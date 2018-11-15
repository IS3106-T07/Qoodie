/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderDish;
import entity.Store;
import error.CustomerOrderNotFoundException;
import error.OrderDishNotFoundException;
import session.CustomerOrderSessionBeanLocal;
import session.CustomerSessionBeanLocal;
import session.StoreSessionBeanLocal;
import webservices.restful.helper.Base64AuthenticationHeaderHelper;
import webservices.restful.helper.Flattener;
import webservices.restful.helper.PATCH;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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
    @EJB
    StoreSessionBeanLocal storeSessionBeanLocal;

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

    // 21 a customer OR A VENDOR edits his/her customer order(this replaces endpoint 7)
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

        boolean isCustomer = false;
        boolean isStore = false;
        List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
        List<Store> storeList = storeSessionBeanLocal.readStoreByEmail(email);
        if (!customerList.isEmpty()) { //CASE: email not in database
            isCustomer = true;
        } else if (!storeList.isEmpty()) {
            isStore = true;
        }
        if (!isCustomer && !isStore) {
            return getUserNotFoundResponse();
        }
        if (isCustomer) {
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
        } else { //isStore == true
            Store store = storeList.get(0);
            if (store.getVendor().getPassword().equals(password)) {
                try {
                    CustomerOrder co = customerOrderSessionBeanLocal.readCustomerOrder(Long.valueOf(coId));

                    if (!co.getOrderDishes().get(0).getDish().getStore().getId().equals(store.getId())) { // check if this order belongs to the sotore
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
