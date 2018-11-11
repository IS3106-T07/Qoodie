/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Customer;
import entity.Dish;
import entity.Store;
import error.CustomerNotFoundException;
import error.StoreNotFoundException;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.DishSessionBeanLocal;
import session.StoreSessionBeanLocal;
import webservices.restful.helper.Flattener;

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
                if (store.getPassword().equals(password)) {
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
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editStore(@PathParam("id") Long sId, Store s) {
        s.setId(sId);
        try {
            storeSessionBeanLocal.updateStore(s);
            return Response.status(204).build();
        } catch (StoreNotFoundException e) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
}
