/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Customer;
import entity.CustomerOrder;
import entity.CustomerOrderType;
import entity.OrderDish;
import entity.Store;
import error.CustomerNotFoundException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.OrderDishNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CustomerOrderSessionBeanLocal;
import session.CustomerOrderTypeSessionBeanLocal;
import session.CustomerSessionBeanLocal;
import session.OrderDishSessionBeanLocal;
import webservices.restful.helper.Base64AuthenticationHeaderHelper;
import webservices.restful.helper.PATCH;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("orderDishes")
public class OrderDishesResource {

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;
    @EJB
    OrderDishSessionBeanLocal orderDishSessionBeanLocal;
    @EJB
    CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionBeanLocal;

    //18 a customer adds a orderDish to cart. 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrderDish(@HeaderParam("Authentication") String authHeader, OrderDish od) throws CustomerOrderNotFoundException, CustomerOrderTypeNotFoundException, CustomerNotFoundException {
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

        if (customer.getPassword().equals(password)) { //correct credantial. can add orderdish to himself
            //add the orderDish to a custoemrOrder if possible
            Store store = od.getDish().getStore();
            if (customer.getCustomerOrders() != null) {
                List<CustomerOrder> customerOrders = customer.getCustomerOrders();
                for (CustomerOrder customerOrder : customerOrders) {
                    if (customerOrder.getOrderDishes().get(0).getDish().getStore() == store) {
                        customerOrder.getOrderDishes().add(od);
                        od.setCustomerOrder(customerOrder);
                        orderDishSessionBeanLocal.createOrderDish(od);
                        customerOrderSessionBeanLocal.updateCustomerOrder(customerOrder);
                        return Response.status(204).build();
                    }
                }
            }
            //add the orderDIsh to a new customer order
            orderDishSessionBeanLocal.createOrderDish(od);
            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setCustomer(customer);
            List<OrderDish> orderDishes = new ArrayList<>();
            orderDishes.add(od);
            customerOrder.setOrderDishes(orderDishes);
            od.setCustomerOrder(customerOrder);
            CustomerOrderType inBasketType = customerOrderTypeSessionBeanLocal.readCustomerOrderTypeByName("in basket").get(0);
            customerOrder.setCustomerOrderType(inBasketType);
            customerOrderSessionBeanLocal.createCustomerOrder(customerOrder);
            inBasketType.getCustomerOrders().add(customerOrder);
            customerOrderTypeSessionBeanLocal.updateCustomerOrderType(inBasketType);
            customer.getCustomerOrders().add(customerOrder);
            customerSessionBeanLocal.updateCustoemr(customer);
            
            return Response.status(204).build();
        } else {
            return getWrongPasswordResponse();
        }
        //TODO: test this method
    }
    
    //19 a customer edits a orderDish in cart
    @PATCH
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
           orderDishSessionBeanLocal.updateOrderDish(od);
           return Response.status(204).build();
        } else {
            return getWrongPasswordResponse();
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

    private Response getWrongPasswordResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "wrong passord")
                .build();
        return Response.status(401).entity(exception)
                .type(MediaType.APPLICATION_JSON).build();
    }

}
