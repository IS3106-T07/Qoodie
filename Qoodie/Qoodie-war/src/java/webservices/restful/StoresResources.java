/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Dish;
import entity.Store;
import error.StoreNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    //8  get all stores
    @GET
    @Produces("application/json")
    public List<Store> getAllCustomers() {
        System.out.println("***** reading all stores *****");
        List<Store> stores = storeSessionBeanLocal.readAllStore();
        for (Store s : stores) {
            for (Dish d : s.getDishes()) {
                d.setStore(null);
            }
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
    public Response createDish(@PathParam("store_id") Long sId, Dish d){
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

}
