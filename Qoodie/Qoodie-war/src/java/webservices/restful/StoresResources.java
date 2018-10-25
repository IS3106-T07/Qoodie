/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package webservices.restful;

import entity.Store;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    
    //2. user get all stores
    @GET
    @Produces("application/json")
    public List<Store> getAllCustomers(){
        System.out.println("***************reading all stores************");
        return storeSessionBeanLocal.readAllStore();
    }
    
}
