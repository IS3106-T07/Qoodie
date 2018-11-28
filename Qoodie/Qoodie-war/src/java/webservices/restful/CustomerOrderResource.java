/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.*;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.OrderDishNotFoundException;
import session.CustomerOrderSessionBeanLocal;
import session.CustomerOrderTypeSessionBeanLocal;
import session.CustomerSessionBeanLocal;
import session.StoreSessionBeanLocal;
import webservices.restful.helper.Base64AuthenticationHeaderHelper;
import webservices.restful.helper.Flattener;
import webservices.restful.helper.PATCH;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webservices.restful.util.ResponseHelper.getExceptionDump;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("customerOrders")

public class CustomerOrderResource {
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionBean = lookupCustomerOrderTypeSessionBeanLocal();
    private LinkedHashMap<Object, Object> error = new LinkedHashMap<>();

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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{order_id}")
    public Response updateCustomerOrder(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("order_id") String coId,
            CustomerOrder customerOrder) throws CustomerOrderTypeNotFoundException, CustomerOrderNotFoundException {
        try {
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
            CustomerOrderType paid = customerOrderTypeSessionBean.readCustomerOrderTypeByName("PAID").get(0);
            List<CustomerOrder> customerOrders = paid.getCustomerOrders();
            if (isCustomer) {
                System.out.println("TEST TEST");
                Customer customer = customerList.get(0);

                if (customer.getPassword().equals(password)) {
                    try {
                        CustomerOrder co = customerOrderSessionBeanLocal.readCustomerOrder(Long.valueOf(coId));

                        if (!co.getCustomer().getId().equals(customer.getId())) { // check if this order belongs to the auth customer
                            return getNoPermissionResponse();
                        }
                        //correct credantial. perform logic
//                    customerOrder.setId(Long.valueOf(coId));
//                    customerOrder.setCustomerOrderType(paid);
                        System.out.println("TEST HERE");
                        co.setLastUpdate(new Date());
                        double realPrice = 0.0;
                        for (OrderDish od : co.getOrderDishes()) {
                            realPrice += od.getAmount() * od.getDish().getPrice();
                        }
                        System.out.println("**** updating the price : " + realPrice);
                        co.setPrice(Double.parseDouble(decimalFormat.format(realPrice)));
                        co.setCustomerOrderType(paid);
                        customerOrderSessionBeanLocal.updateCustomerOrderNonNullFields(co);
                        customerOrders.add(co);
                        paid.setCustomerOrders(customerOrders);
                        customerOrderTypeSessionBean.updateCustomerOrderType(paid);
                        return Response.status(204).build();

                    } catch (CustomerOrderNotFoundException e) {
                        return getNotFoundResponse();
                    }
                } else {
                    return getWrongPasswordResponse();//TODO: test this endpoint
                }
            } else { //isStore == true
                Store store = storeList.get(0);
                System.out.println("HERE");
                if (store.getVendor().getPassword().equals(password)) {
                    try {
                        CustomerOrder co = customerOrderSessionBeanLocal.readCustomerOrder(Long.valueOf(coId));

                        if (!co.getOrderDishes().get(0).getDish().getStore().getId().equals(store.getId())) { // check if this order belongs to the sotore
                            System.out.println("HERE 2");
                            return getNoPermissionResponse();
                        }
                        //correct credential. perform logic
                        customerOrder.setId(Long.valueOf(coId));
                        customerOrder.setCustomerOrderType(paid);
                        customerOrderSessionBeanLocal.updateCustomerOrderNonNullFields(customerOrder);
                        customerOrders.add(customerOrder);
                        paid.setCustomerOrders(customerOrders);
                        customerOrderTypeSessionBean.updateCustomerOrderType(paid);
                        System.out.println("HERE 3");
                        return Response.status(204).build();
                    } catch (CustomerOrderNotFoundException e) {
                        return getNotFoundResponse();
                    }
                } else {
                    return getWrongPasswordResponse();//TODO: test this endpoint
                }
            }
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
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

    private CustomerOrderTypeSessionBeanLocal lookupCustomerOrderTypeSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CustomerOrderTypeSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/CustomerOrderTypeSessionBean!session.CustomerOrderTypeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
