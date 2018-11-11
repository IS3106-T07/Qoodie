/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.CustomerOrder;
import entity.OrderDish;
import javax.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import session.CustomerOrderSessionBeanLocal;
import session.OrderDishSessionBeanLocal;

/**
 *
 * @author DEEP
 */
@ManagedBean
//@Named(value = "viewOrders")
@ViewScoped
public class viewOrders implements Serializable {

    
    private boolean showIncoming;
    private boolean showCompleted;
    private long storeId;
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
        storeId = 18;
        incomingOrders = new ArrayList<OrderDish>();
        completed = new ArrayList<OrderDish>();
        seperateOrders();
    }

    
    public void seperateOrders() {

        System.out.println("TES BEAN " + orderDishSessionLocal);
        List<OrderDish> orders = orderDishSessionLocal.getStoreOrder(getStoreId());
        System.out.println("retrieved" + orders);
        for (OrderDish o : orders) {
            //   System.out.println("order status - "+ o.getCustomerOrder().getCustomerOrderType().getName());
            if (o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("paid")) {
                incomingOrders.add(o);
                System.out.println("Incoming - " + incomingOrders);
            }
            if (o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("delivered")) {
                completed.add(o);
                System.out.println("Completed - " + completed);
            }
        }
    }

//    public double getRevenue(storeId, date, date) {
//        return customerOrderSessionBean.calculateRevenue(storeId, date, date);
//    }
//
//    public List<CustomerOrder> getOrders(storeId, date, date) {
//        return customerOrderSessionBean.orderbyDate(storeId, date, date);
//    }

    /**
     * @return the storeId
     */
    public Long getStoreId() {
        return storeId;
    }

    /**
     * @return the incomingOrders
     */
    public void updateOrders() {
        System.out.println("Latest Orders Retrieved");
        seperateOrders();
    }

    /**
     * @return the completed
     */
    public List<OrderDish> getCompleted() {
//        updateOrders();
        return completed;
    }

    /**
     * @param storeId the storeId to set
     */
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(List<OrderDish> completed) {
        this.completed = completed;
    }

//    public Date getDate() {
//        return date;
//    }
//
//    /**
//     * @param date the date to set
//     */
//    public void setDate(Date date) {
//        this.date = date;
//        System.out.println("after setting date - " + date);
//    }

    /**
     * @return the incomingOrders
     */
    public List<OrderDish> getIncomingOrders() {

        return incomingOrders;
    }

    /**
     * @param incoming the incomingOrders to set
     */
    public void setIncomingOrders(List<OrderDish> incomingOrders) {

        this.incomingOrders = incomingOrders;
    }
}
