///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
// package ManagedBeans;
//
//
//import entity.OrderDish;
//import javax.inject.Named;
//
//import java.io.Serializable;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import javax.ejb.EJB;
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.SessionScoped;
//import javax.faces.context.FacesContext;
//import org.primefaces.context.RequestContext;
//import org.primefaces.event.SelectEvent;
//import session.OrderDishSessionBeanLocal;
//
///**
// *
// * @author DEEP
// */
//@Named(value = "viewOrders")
//@SessionScoped
//public class viewOrders implements Serializable {
//
//   private Date date;
//   private boolean showIncoming;
//   private boolean showCompleted;
//   private long storeId;
//   private List<OrderDish> incoming;
//   private List<OrderDish> completed;
//   @EJB
//   OrderDishSessionBeanLocal vendorsession;
//   
//    public viewOrders() {
//        storeId=17;
//        incoming = new ArrayList<OrderDish>();
//        completed = new ArrayList<OrderDish>();
//        showIncoming=false;
//        showCompleted=false;
//    }
//    
//    public void seperateOrders()
//    {
//        List<OrderDish> orders = vendorsession.getStoreOrder(getStoreId());
//        for(OrderDish o :orders)
//        {
//            if(o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("paid"))
//                incoming.add(o);
//            else
//                if(o.getCustomerOrder().getCustomerOrderType().getName().toLowerCase().equals("delivererd"))
//                completed.add(o);
//        }
//    }
//  
//    public void onDateSelect(SelectEvent event) {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
//    }
//     
//    public void click() {
//      RequestContext requestContext = RequestContext.getCurrentInstance();  
//      requestContext.update("form:display");  
//      requestContext.execute("PF('dlg').show()");  
//    }
// 
//    
//    
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
//        seperateOrders();
//        return incoming;
//    }
//
//    /**
//     * @return the completed
//     */
//    public List<OrderDish> getCompleted() {
//        seperateOrders();
//        return completed;
//    }
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
//        this.incoming = incoming;
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
//     * @return the showIncoming
//     */
//    public boolean isShowIncoming() {
//        return showIncoming;
//    }
//
// 
//    public boolean isShowCompleted() {
//        return showCompleted;
//    }
//
//    public void setShowIncoming(boolean showIncoming) {
//        this.showIncoming = showIncoming;
//    }
//
//    public void setShowCompleted(boolean showCompleted) {
//        this.showCompleted = showCompleted;
//    }
//    public String switchIncoming()
//    {
//        System.out.println("button clicked ="+showIncoming );
//        if(showIncoming==true)
//          showIncoming=false;
//        else
//            showIncoming=true;
//        return "index.xhtml";
//    }
//
//    /**
//     * @return the date
//     */
//    public Date getDate() {
//        return date;
//    }
//
//    /**
//     * @param date the date to set
//     */
//    public void setDate(Date date) {
//        this.date = date;
//    }
//}
//   /**  public String showIncoming()
//    {
//       setShowIncoming(true);
//        return "index.xhtml";
//    }
//    public String hideIncoming()
//    {
//        setShowIncoming(false);
//        return "index.xhtml";
//    }
//    public String showCompleted()
//    {
//        setShowCompleted(true);
//        return "index.xhtml";
//    }
//    public String hideCompleted()
//    {
//        setShowCompleted(false);
//        return "index.xhtml";
//    }
//    
//**/
