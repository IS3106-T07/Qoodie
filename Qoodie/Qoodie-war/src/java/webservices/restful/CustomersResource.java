/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.*;
import error.CustomerNotFoundException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.OrderDishNotFoundException;
import session.*;
import webservices.restful.datamodels.CartUpdateReq;
import webservices.restful.datamodels.UpdateBookmarkReq;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static webservices.restful.util.ResponseHelper.getExceptionDump;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("customers")
public class CustomersResource {
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionBean = lookupCustomerOrderTypeSessionBeanLocal();
    DishSessionBeanLocal dishSessionBean = lookupDishSessionBeanLocal();
    OrderDishSessionBeanLocal orderDishSessionBeanLocal = lookupOrderDishSessionBeanLocal();
    private LinkedHashMap<Object, Object> error = new LinkedHashMap<>();

    @EJB
    CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

    // 1 admin get all customers
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerSessionBeanLocal.readAllCustomer();
        for (Customer c : customers) {
            c = Flattener.flatten(c);
        }
        return customers;
    }

    // 2 Create a new Customer by using the request body data.
    // Should return empty payload if the creation is successful (204)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer c) {
        customerSessionBeanLocal.createCustomer(c);
        return Response.status(204).build();
    }

    // 3 get a customer
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long cId) {
        try {
            Customer c = customerSessionBeanLocal.readCustomer(cId);
            return Response.status(200).entity(Flattener.flatten(c)).type(MediaType.APPLICATION_JSON).build();
        } catch (CustomerNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder().add("message", "customer not found").build();
            return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
        }
    }

    // 4 edit a customer.
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editCustomer(@HeaderParam("Authorization") String authHeader, Customer newC) {

        String email = Base64AuthenticationHeaderHelper.getUsernameOrErrorResponseString(authHeader);
        if (email.toLowerCase().contains("not found")) {
            JsonObject exception = Json.createObjectBuilder().add("message", "authentication informaiton not found")
                    .build();
            return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
        }
        String password = Base64AuthenticationHeaderHelper.getPasswordOrErrorResponseString(authHeader);

        List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
        if (customerList.isEmpty()) // CASE: email not in database
        {
            JsonObject exception = Json.createObjectBuilder().add("message", "user not found").build();
            return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
        }

        Customer c = customerList.get(0);
        newC.setId(c.getId());
        if (c.getPassword().equals(password)) { // correct credantial. can edit
            try {
                customerSessionBeanLocal.updateCustomer(newC);
                return Response.status(204).build();
            } catch (CustomerNotFoundException e) {
                JsonObject exception = Json.createObjectBuilder().add("error", "Not found").build();
                return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
            }
        } else {
            JsonObject exception = Json.createObjectBuilder().add("message", "wrong passord").build();
            return Response.status(401).entity(exception).type(MediaType.APPLICATION_JSON).build();
        }
    }

    // 5
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("id") Long cId) {
        try {
            Customer c = customerSessionBeanLocal.readCustomer(cId);
            customerSessionBeanLocal.deleteCustomer(c);
            return Response.status(204).build();
        } catch (CustomerNotFoundException e) {
            JsonObject exception = Json.createObjectBuilder().add("error", "Not found").build();
            return Response.status(404).entity(exception).build();
        }
    }

    // 6 create a cusotomer Order (type: in baseket)for a customer
    @POST
    @Path("/{customer_id}/orders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomerOrder(@PathParam("customer_id") Long cId, CustomerOrder o) {
        try {
            customerOrderSessionBeanLocal.createCustomerOrder(o);
            CustomerOrderType inBasket = customerOrderTypeSessionBean.readCustomerOrderTypeByName("IN BASKET").get(0);
            o.setCustomerOrderType(inBasket);
            Customer customer = customerSessionBeanLocal.readCustomer(cId);
            List<CustomerOrder> customerOrders = inBasket.getCustomerOrders();
            customerOrders.add(o);
            customerOrderTypeSessionBean.updateCustomerOrderType(inBasket);
            //add association
            o.setCustomer(customer);
            customer.getCustomerOrders().add(o);
            // update both entities
            customerOrderSessionBeanLocal.updateCustomerOrder(o);
            customerSessionBeanLocal.updateCustomer(customer);
            return Response.status(200).entity(Flattener.flatten(customer)).build();
        } catch (CustomerNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder().add("error", "Customer not found").build();
            return Response.status(404).entity(exception).build();
        } catch (CustomerOrderTypeNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder().add("error", "Customer  order type not found").build();
            return Response.status(404).entity(exception).build();
        } catch (CustomerOrderNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder().add("error", "Customer order not found").build();
            return Response.status(404).entity(exception).build();
        } catch (OrderDishNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder().add("error", "Order Dish not found").build();
            return Response.status(404).entity(exception).build();
        }
    }

    @POST
    @Path("updateBookmark")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBookmark(UpdateBookmarkReq updateBookmarkReq) {
        try {
            Customer customer = customerSessionBeanLocal.readCustomer(updateBookmarkReq.getCustomerId());
            if (customer == null) {
                error.put("message", "Unauthorised user");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            customer.setBookmarkString(updateBookmarkReq.getBookmarkString());
            customerSessionBeanLocal.updateCustomer(customer);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("addItemToCart")
    public Response addItemToCart(CartUpdateReq cartUpdateReq){
        try {
            Customer customer = customerSessionBeanLocal.readCustomer(cartUpdateReq.getCustomerId());
            if (customer == null) {
                error.put("message", "Unauthorised user");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
            }
            CustomerOrderType inBasket = customerOrderTypeSessionBean.readCustomerOrderTypeByName("IN BASKET").get(0);
            List<CustomerOrder> customerOrders = customer.getCustomerOrders();
            System.out.println("size " + customerOrders.size());
//            List<CustomerOrder> inBasketCo = customerOrders.stream().filter(co -> co.getCustomerOrderType().getName().equals("IN BASKET")).collect(Collectors.toList());
            List<CustomerOrder> inBasketCo = new ArrayList<>();
            for (CustomerOrder customerOrder : customerOrders) {
                if (customerOrder.getCustomerOrderType().getName().equals("IN BASKET")) {
                    inBasketCo.add(customerOrder);
                }
            }
            System.out.println("Filter " + inBasketCo.size());
            CustomerOrder customerOrder = null;
            try {
                customerOrder = inBasketCo.get(0);
            } catch (Exception e) {
                customerOrder = null;
            }
            Dish dish = dishSessionBean.readDish(cartUpdateReq.getDishId());
            double price = Double.parseDouble(decimalFormat.format(cartUpdateReq.getQuantity() * dish.getPrice()));
            if (customerOrder == null) {
                customerOrder = new CustomerOrder();
                customerOrder.setCustomerOrderType(inBasket);
                customerOrder.setCustomer(customer);
                customerOrder.setCreated(new Date());
                customerOrder.setLastUpdate(new Date());
                customerOrder.setPrice(price);
                customerOrderSessionBeanLocal.createCustomerOrder(customerOrder);
                OrderDish orderDish = new OrderDish();
                orderDishSessionBeanLocal.createOrderDish(orderDish);
                setOrderDish(cartUpdateReq.getQuantity(), customerOrder, dish, orderDish, true);
                customerOrders.add(customerOrder);
            } else {
                customerOrder.setLastUpdate(new Date());
                double orgPrice = Math.max(0, customerOrder.getPrice());
                customerOrder.setPrice(Double.parseDouble(decimalFormat.format(orgPrice + price)));
                customerOrderSessionBeanLocal.updateCustomerOrder(customerOrder);
                List<Dish> dishes = customerOrder.getOrderDishes().stream().map(o -> o.getDish()).collect(Collectors.toList());
                System.out.println(dishes);
                List<OrderDish> orderDishes = new ArrayList<>();
                for (OrderDish orderDish : customerOrder.getOrderDishes()) {
                    if (orderDish.getDish().getId().equals(cartUpdateReq.getDishId())) orderDishes.add(orderDish);
                }
                long count = orderDishes.size();
                System.out.println(count);
                if (count == 0) {
                    System.out.println("New Dish");
                    OrderDish orderDish = new OrderDish();
                    orderDishSessionBeanLocal.createOrderDish(orderDish);
                    setOrderDish(cartUpdateReq.getQuantity(), customerOrder, dish, orderDish, true);
                } else {
                    System.out.println("Old Dish");
                    OrderDish orderDish = orderDishes.get(0);
                    System.out.println(orderDish);
                    setOrderDish(cartUpdateReq.getQuantity() + orderDish.getAmount(),
                            customerOrder, dish, orderDish, false);
                }
            }
            customerOrderSessionBeanLocal.updateCustomerOrder(customerOrder);
            System.out.println("Order Dishes: " + customerOrder.getOrderDishes().size());
            customer.setCustomerOrders(customerOrders);
            customerSessionBeanLocal.updateCustomer(customer);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    private void setOrderDish(Integer amount, CustomerOrder customerOrder, Dish dish, OrderDish orderDish, boolean isNew)
            throws OrderDishNotFoundException {
        orderDish.setDish(dish);
        orderDish.setAmount(amount);
        orderDish.setCustomerOrder(customerOrder);
        orderDishSessionBeanLocal.updateOrderDish(orderDish);
        if (isNew) {
            List<OrderDish> orderDishes = customerOrder.getOrderDishes();
            orderDishes.add(orderDish);
            customerOrder.setOrderDishes(orderDishes);
        }
    }

    /*
     * //7 update tthe cusotmer order type to : PAID
     * 
     * @GET
     * 
     * @Path("/{order_id}/pay") public Response payOrder(@PathParam("order_id") Long
     * oId ) { try { CustomerOrder o =
     * customerOrderSessionBeanLocal.readCustomerOrder(oId);
     * customerOrderSessionBeanLocal.payCustomerOrder(o); return
     * Response.status(200).entity(Flattener.flatten(o)).build(); } catch
     * (CustomerOrderNotFoundException ex) { JsonObject exception =
     * Json.createObjectBuilder() .add("message", "order not found") .build();
     * return Response.status(404).entity(exception)
     * .type(MediaType.APPLICATION_JSON).build(); } catch
     * (CustomerOrderTypeNotFoundException ex) { JsonObject exception =
     * Json.createObjectBuilder() .add("message", "order type not found") .build();
     * return Response.status(404).entity(exception)
     * .type(MediaType.APPLICATION_JSON).build(); } catch
     * (CustomerOrderAlreadyPaidException ex) { JsonObject exception =
     * Json.createObjectBuilder() .add("message", "order already paid") .build();
     * return Response.status(404).entity(exception)
     * .type(MediaType.APPLICATION_JSON).build(); } }
     */
    // 12
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || authHeader.length() == 0) { // CASE: no auth token in the header
            JsonObject exception = Json.createObjectBuilder().add("message", "authentication informaiton not found")
                    .build();
            return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
        } else {
            String authToken = authHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String decodedString = new String(Base64.getDecoder().decode(authToken));
            StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
            String email = tokenizer.nextToken();
            String password = tokenizer.nextToken();

            List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
            if (customerList.isEmpty()) // CASE: wrong email or password
            {
                JsonObject exception = Json.createObjectBuilder().add("message", "user not found").build();
                return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
            } else {
                Customer customer = customerList.get(0);
                if (customer.getPassword().equals(password)) {
                    return Response.status(200).entity(Flattener.flatten(customer)).type(MediaType.APPLICATION_JSON)
                            .build();
                } else {
                    JsonObject exception = Json.createObjectBuilder().add("message", "wrong passord").build();
                    return Response.status(401).entity(exception).type(MediaType.APPLICATION_JSON).build();
                }
            }
        }
    }

    // 22 a customer gets his/her customer order with the all types: DELIVERED, IN
    // BASKET and PAID
    @GET
    @Path("/orders")
    public Response getAllCustomerOrders(@HeaderParam("Authorization") String authHeader) {
        try {
            String email = Base64AuthenticationHeaderHelper.
                    getUsernameOrErrorResponseString(authHeader);
            if (email.toLowerCase().contains("not found")) {
                error.put("message", "No email provided");
                return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
            }
            String password = Base64AuthenticationHeaderHelper.
                    getPasswordOrErrorResponseString(authHeader);

            List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
            if (customerList.isEmpty()) {
                error.put("message", "Unauthorised User");
                return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
            }
            Customer customer = customerList.get(0);
            if (customer.getPassword().equals(password)) {//correct credential. perform logic
                List<CustomerOrder> customerOrders = customer.getCustomerOrders();
                for (CustomerOrder customerOrder: customerOrders){
                    Flattener.flatten(customerOrder);
                }
                return Response.ok(new GenericEntity<List<CustomerOrder>>(customerOrders){}).build();

            } else {
                error.put("message", "Wrong Password");
                return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
            }
        } catch (Exception ex) {
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    private Response getAuthNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder().add("message", "authentication informaiton not found")
                .build();
        return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
    }

    private Response getUserNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder().add("message", "user not found").build();
        return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
    }

    private Response getNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder().add("message", "not found").build();
        return Response.status(404).entity(exception).type(MediaType.APPLICATION_JSON).build();
    }

    private Response getWrongPasswordResponse() {
        JsonObject exception = Json.createObjectBuilder().add("message", "wrong passord").build();
        return Response.status(401).entity(exception).type(MediaType.APPLICATION_JSON).build();
    }

    private Response getNoPermissionResponse() {
        JsonObject exception = Json.createObjectBuilder().add("message", "no permission").build();
        return Response.status(403).entity(exception).type(MediaType.APPLICATION_JSON).build();
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

    private DishSessionBeanLocal lookupDishSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DishSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/DishSessionBean!session.DishSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private OrderDishSessionBeanLocal lookupOrderDishSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (OrderDishSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/OrderDishSessionBean!session.OrderDishSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
