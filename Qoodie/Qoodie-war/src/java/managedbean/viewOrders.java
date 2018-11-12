/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

  package managedbean;


import entity.OrderDish;
import entity.Store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import session.OrderDishSessionBeanLocal;
import session.StoreSessionBeanLocal;

/**
 *
 * @author DEEP
 */
@ManagedBean
@ViewScoped
public class viewOrders implements Serializable {
    @EJB
    private StoreSessionBeanLocal storeSessionBeanLocal;

   private Date date;
   private boolean showIncoming;
   private boolean showCompleted;
   private Store store;
   private List<OrderDish> incomingOrders;
   private List<OrderDish> completed;
   @EJB
   OrderDishSessionBeanLocal orderDishSessionLocal;
   
    public viewOrders() {
        System.out.println("HERE HERE");
    }
    
    @PostConstruct
    public void init() {
        System.out.println("TES TEST");
        setStore(storeSessionBeanLocal.readStoreByEmail("vendor1@gmail.com").get(0));
        incomingOrders = new ArrayList<OrderDish>();
        completed = new ArrayList<OrderDish>();
        seperateOrders();
    }
    
    public void seperateOrders()
    {
        
        List<OrderDish> orders = orderDishSessionLocal.getStoreOrder(getStore().getId());
        System.out.println("retrieved" + orders);
        for(OrderDish o :orders)
        {
            System.out.println("hello we are now separating orders");
         //   System.out.println("order status - "+ o.getCustomerOrder().getCustomerOrderType().getName());
            if(o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("paid"))
            {
                incomingOrders.add(o);
                System.out.println("Incoming - " + incomingOrders);
            }
            if(o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("delivered"))
                {
                    completed.add(o);
                    System.out.println("Completed - " + completed);
                }
        }
    }
    /**
     * @return the storeId
     */
    
    /**
     * @return the incoming
     */
    public List<OrderDish> getIncoming() {
         seperateOrders();
        return incomingOrders;
    }


    /**
     * @param storeId the storeId to set
     */
    
    /**
     * @param incoming the incoming to set
     */
    public void setIncoming(List<OrderDish> incoming) {
        this.incomingOrders = incoming;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(List<OrderDish> completed) {
        this.completed = completed;
    }

    /**
     * @return the completed
     */
    public List<OrderDish> getCompleted() {
        return completed;
    }

    /**
     * @return the store
     */
    public Store getStore() {
        return store;
    }

    /**
     * @param store the store to set
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * @return the showIncoming
     */
  
    /**
     * @return the date
     */
}