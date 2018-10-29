/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package webservices.restful;

import entity.Dish;
import entity.Store;
import error.StoreNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import session.StoreSessionBeanLocal;

/**
 * REST Web Service
 *
 * @author alex_zy
 */

@Path("stores")
public class StoresResources {
    @EJB
            StoreSessionBeanLocal storeSessionBeanLocal;
    
    //8  get all stores
    @GET
    @Produces("application/json")
    public List<Store> getAllCustomers(){
        System.out.println("***** reading all stores *****");
        List<Store> stores =  storeSessionBeanLocal.readAllStore();
        for (Store s: stores){
            for (Dish d: s.getDishes())
                d.setStore(null);
        }
        return stores;
    }
    
    //9 get all dishes from one store
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getAllDishes(@PathParam("id") Long sId){
        System.out.println("***** reading from one store *****");
        try {
            List<Dish> dishes = storeSessionBeanLocal.readStore(sId).getDishes();
            JsonArrayBuilder builder = Json.createArrayBuilder();
            
            for (Dish d: dishes) {
                JsonObject value  = Json.createObjectBuilder()
                        .add("id", d.getId())
                        .add("name", d.getName())
                        .add("description", d.getDescription())
                        .add("price", d.getPrice())
                        .add("store_id", d.getStore().getId())
                        .add("store_name", d.getStore().getName())
                        .add("availability", d.getIsAvailable())
                        //.add("type_id", d.getDishType().getId())
                        //.add("type_name",d.getDishType().getName()) //TODO: UNSEPCIFIED dish type 
                        .build();
                builder.add(value);
            }
            JsonArray result = builder.build();
            return Response.status(200).entity(result).build();
            
        } catch (StoreNotFoundException ex) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Store not found")
                    .build();
            return Response.status(404).entity(exception).build();
        }
    }
    
}
