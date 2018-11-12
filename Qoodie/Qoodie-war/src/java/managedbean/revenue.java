/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.CustomerOrder;
import entity.OrderDish;
import entity.Store;
import error.StoreNotFoundException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import session.CustomerOrderSessionBeanLocal;
import session.StoreSessionBeanLocal;

/**
 *
 * @author DEEP
 */
@ManagedBean
@SessionScoped
public class revenue implements Serializable {

    private Date date;
    private Date weekFirstDay;
    private Date weekLastDay;
    private Store store;
    Calendar cal;
     @EJB
    private CustomerOrderSessionBeanLocal customerOrderSessionBean;
@EJB
    private StoreSessionBeanLocal storeSessionBean;
    
    @PostConstruct
    public void init()
    {
            setStore(storeSessionBean.readStoreByEmail("vendor1@gmail.com").get(0));
    }
    public revenue() {
          this.date = new Date("11/20/2018");
//          System.out.println("****"+customerOrderSessionBean.readAllCustomerOrder().size());

          cal = Calendar.getInstance();
    }
    public void handleDateSelect(SelectEvent event) {
        setDate((Date) event.getObject());
        
        cal.setTime(getDate());
        cal.set(Calendar.DAY_OF_WEEK, 1);
        weekFirstDay = cal.getTime(); 
        cal.set(Calendar.DAY_OF_WEEK, 7);
        weekLastDay = cal.getTime();
        
        System.out.println("date chosen = "+ getDate());
        System.out.println("first Day of Week = "+ getWeekFirstDay());
        System.out.println("Last Day of Week = "+ getWeekLastDay());
        
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Date is" + getDate().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<CustomerOrder> dayOrders() throws StoreNotFoundException
    {
        System.out.println(store.getName());
        
        return customerOrderSessionBean.getStoreCustomerOrder(store.getId(), date, date);
    }
    public List<CustomerOrder> weekOrders() throws StoreNotFoundException
    {
        return customerOrderSessionBean.getStoreCustomerOrder(store.getId(), weekFirstDay, weekLastDay);
    }
    public String dishList(List<OrderDish> list)
    {
        String str="";
        for(OrderDish i : list)
        {
            str=str+ i.getDish().getName()+","; 
        }
        return str.substring(0, str.length()-1);
    }
    public double getDayRevenue() throws StoreNotFoundException
    {
        return customerOrderSessionBean.calculateRevenue(store.getId(), date, date);
    }
    public double getWeekRevenue() throws StoreNotFoundException
    {
        return customerOrderSessionBean.calculateRevenue(store.getId(), weekFirstDay, weekLastDay);
    }
    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }


    /**
     * @return the weekFirstDay
     */
    public Date getWeekFirstDay() {
        return weekFirstDay;
    }

    /**
     * @return the weekLastDay
     */
    public Date getWeekLastDay() {
        return weekLastDay;
    }

    /**
     * @param weekFirstDay the weekFirstDay to set
     */
    public void setWeekFirstDay(Date weekFirstDay) {
        this.weekFirstDay = weekFirstDay;
    }

    /**
     * @param weekLastDay the weekLastDay to set
     */
    public void setWeekLastDay(Date weekLastDay) {
        this.weekLastDay = weekLastDay;
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

    
}