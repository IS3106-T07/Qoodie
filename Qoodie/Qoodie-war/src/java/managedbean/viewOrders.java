///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//  package managedbean;
//
//
//import entity.OrderDish;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.ejb.EJB;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ViewScoped;
//import session.OrderDishSessionBeanLocal;
//
///**
// *
// * @author DEEP
// */
//@ManagedBean
//@ViewScoped
//public class viewOrders implements Serializable {
//
//   private Date date;
//   private boolean showIncoming;
//   private boolean showCompleted;
//   private long storeId;
//   private List<OrderDish> incomingOrders;
//   private List<OrderDish> completed;
//   @EJB
//   OrderDishSessionBeanLocal orderDishSessionLocal;
//   
//    public viewOrders() {
//        System.out.println("HERE HERE");
//    }
//    
//    @PostConstruct
//    public void init() {
//        System.out.println("TES TEST");
//        storeId=18;
//        incomingOrders = new ArrayList<OrderDish>();
//        completed = new ArrayList<OrderDish>();
//        seperateOrders();
//    }
//    
//    public void seperateOrders()
//    {
//        
//        List<OrderDish> orders = orderDishSessionLocal.getStoreOrder(getStoreId());
//        System.out.println("retrieved" + orders);
//        for(OrderDish o :orders)
//        {
//            System.out.println("hello we are now separating orders");
//         //   System.out.println("order status - "+ o.getCustomerOrder().getCustomerOrderType().getName());
//            if(o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("paid"))
//            {
//                incomingOrders.add(o);
//                System.out.println("Incoming - " + incomingOrders);
//            }
//            if(o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("delivered"))
//                {
//                    completed.add(o);
//                    System.out.println("Completed - " + completed);
//                }
//        }
//    }
//    /**
//     * @return the storeId
//     */
//    public Long getStoreId() {
//        return storeId;
//    }
//
//    /**
//     * @return the incoming
//     */
//    public List<OrderDish> getIncoming() {
//         seperateOrders();
//        return incomingOrders;
//    }
//
//
//    /**
//     * @param storeId the storeId to set
//     */
//    public void setStoreId(Long storeId) {
//        this.storeId = storeId;
//    }
//
//    /**
//     * @param incoming the incoming to set
//     */
//    public void setIncoming(List<OrderDish> incoming) {
//        this.incomingOrders = incoming;
//    }
//
//    /**
//     * @param completed the completed to set
//     */
//    public void setCompleted(List<OrderDish> completed) {
//        this.completed = completed;
//    }
//
//    /**
//     * @return the completed
//     */
//    public List<OrderDish> getCompleted() {
//        return completed;
//    }
//
//    /**
//     * @return the showIncoming
//     */
//  
//    /**
//     * @return the date
//     */
//}