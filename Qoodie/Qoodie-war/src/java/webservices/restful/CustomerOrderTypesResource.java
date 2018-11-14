/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.CustomerOrderType;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import session.CustomerOrderTypeSessionBean;
import session.CustomerOrderTypeSessionBeanLocal;
import webservices.restful.helper.Flattener;

/**
 * REST Web Service
 *
 * @author alex_zy
 */
@Path("customerOrderTypes")
@RequestScoped
public class CustomerOrderTypesResource {

    @EJB
    CustomerOrderTypeSessionBeanLocal CustomerOrderTypeSessionBeanLocal;
            
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerOrderType> getAllCustomerOrderTypes(){
        List<CustomerOrderType> allTypes= CustomerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
        for (CustomerOrderType type : allTypes){
            type.setCustomerOrders(null);
        }
        return allTypes;
    }
}
