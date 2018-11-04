/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Canteen;
import entity.Dish;
import entity.Store;
import error.CanteenNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CanteenSessionBeanLocal;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("canteens")
public class CanteensResources {

    @EJB
    CanteenSessionBeanLocal canteenSessionBeanLocal;

    //10 
    @GET
    @Produces("application/json")
    public List<Canteen> getAllCanteens() {
        List<Canteen> canteens = canteenSessionBeanLocal.readAllCanteen();
        for (Canteen c : canteens) {
            for (Store s : c.getStores()) {
                s.setCanteen(null);
                for(Dish d: s.getDishes())
                    d.setStore(null);
            }
        }
        return canteens;
    }

    //11
    @GET
    @Path("/{id}")
    public Response getCanteen(@PathParam("id") Long cId) {
        try {
            Canteen c = canteenSessionBeanLocal.readCanteen(cId);
            for (Store s : c.getStores()) {
                s.setCanteen(null);
                s.setPassword(null);
                s.setVendorEmail(null);
                for(Dish d: s.getDishes())
                    d.setStore(null);
            }
            return Response.status(200).entity(
                    c
            ).type(MediaType.APPLICATION_JSON).build();
        } catch (CanteenNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("message", "canteen not found")
                    .build();
            return Response.status(404).entity(exception)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

}
