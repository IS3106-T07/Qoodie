/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.CustomerOrder;
import entity.Dish;
import entity.OrderDish;
import entity.Store;
import error.StoreNotFoundException;
import session.DishSessionBeanLocal;
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
import java.util.*;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("stores")
public class StoresResources {

    @EJB
    StoreSessionBeanLocal storeSessionBeanLocal;
    @EJB
    DishSessionBeanLocal dishSessionBeanLocal;

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

    //8  get all stores
    @GET
    @Produces("application/json")
    public List<Store> getAllStores() {
        System.out.println("***** reading all stores *****");
        List<Store> stores = storeSessionBeanLocal.readAllStore();
        for (Store s : stores) {
            s = Flattener.flatten(s);
        }
        return stores;
    }

    //9 get one store
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getAllDishes(@PathParam("id") Long sId) {
        try {
            Store s = storeSessionBeanLocal.readStore(sId);

            return Response.status(200).entity(Flattener.flatten(s)).type(MediaType.APPLICATION_JSON).build();
        } catch (StoreNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "store not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }

    }

    //12 create dish 
    // /{store_id}/dishes
    @POST
    @Path("{store_id}/dishes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDish(@PathParam("store_id") Long sId, Dish d) {
        try {
            Store s = storeSessionBeanLocal.readStore(sId);
            s.getDishes().add(d);
            d.setStore(s);
            dishSessionBeanLocal.createDish(d);
            storeSessionBeanLocal.updateStore(s);
            return Response.status(204).build();
        } catch (StoreNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Store not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

    //13 create store
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createStore(Store s) {
        storeSessionBeanLocal.createStore(s);
        return Response.status(204).build();
    }

    //14 login as a store owner with basic 64 encode : Basic {[email]:[password]}
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("Authorization") String authHeader) {
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

            List<Store> storeList = storeSessionBeanLocal.readStoreByEmail(email);
            if (storeList.isEmpty()) //CASE: wrong email or password
            {
                JsonObject exception = Json.createObjectBuilder()
                        .add("message", "store not found")
                        .build();
                return Response.status(404).entity(exception)
                        .type(MediaType.APPLICATION_JSON).build();
            } else {
                Store store = storeList.get(0);
                if (store.getVendor().getPassword().equals(password)) {
                    return Response.status(200).entity(Flattener.flatten(store))
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

    //15 edit a store
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editStore(@HeaderParam("Authorization") String authHeader, Store newS) {
        String email = Base64AuthenticationHeaderHelper.getPasswordOrErrorResponseString(authHeader);
        if (email.toLowerCase().contains("not found")) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "authentication informaiton not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
        String password = Base64AuthenticationHeaderHelper.getPasswordOrErrorResponseString(authHeader);

        List<Store> storeList = storeSessionBeanLocal.readStoreByEmail(email);
        if (storeList.isEmpty()) //CASE: email not in database
        {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "store not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
        Store oldS = storeList.get(0);
        if (oldS.getVendor().getPassword().equals(password)) { //correct credantial. can edit
            try {
                newS.setId(oldS.getId());
                storeSessionBeanLocal.updateStore(newS);
                return Response.status(204).build();
            } catch (StoreNotFoundException e) {
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

    //23 a store gets all its orders (in bask, delivered and undelivered)
    @GET
    @Path("/orders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllOrders(@HeaderParam("Authorization") String authHeader) {
        String email = Base64AuthenticationHeaderHelper.
                getUsernameOrErrorResponseString(authHeader);
        if (email.toLowerCase().contains("not found")) {
            return getAuthNotFoundResponse();
        }
        String password = Base64AuthenticationHeaderHelper.
                getPasswordOrErrorResponseString(authHeader);

        List<Store> storeList = storeSessionBeanLocal.readStoreByEmail(email);
        if (storeList.isEmpty()) //CASE: email not in database
        {
            return getStoreNotFoundResponse();
        }

        Store store = storeList.get(0);

        if (store.getVendor().getPassword().equals(password)) { //correct credential, perform logic
            List<CustomerOrder> allCustomerOrders = new ArrayList<>();

            for (Dish d : store.getDishes()) {
                System.out.println(d.toString());
                System.out.printf("this dish has %d ordered dish\n", d.getOrderDishes().size());
                for (OrderDish od : d.getOrderDishes()) {
                    System.out.println(od.toString());
                    allCustomerOrders.add(od.getCustomerOrder());
                }
            }
            //remove the repeated customer orders with hashset
            HashSet<CustomerOrder> hs = new HashSet<>();
            hs.addAll(allCustomerOrders);
            allCustomerOrders.clear();
            allCustomerOrders.addAll(hs);

            for (CustomerOrder co : allCustomerOrders) {
                co = Flattener.flatten(co);
                co.getCustomer().setPassword(null);
            }
            GenericEntity<List<CustomerOrder>> allCustomerOrdersGeneric
                    = new GenericEntity<List<CustomerOrder>>(allCustomerOrders) {
                    };

//            return Response.ok(new GenericEntity<List<CustomerOrder>>(allCustomerOrders){}).build();
            return Response.status(200).entity(allCustomerOrdersGeneric)
                    .type(MediaType.APPLICATION_JSON).build();
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

    private Response getStoreNotFoundResponse() {
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "store not found")
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
