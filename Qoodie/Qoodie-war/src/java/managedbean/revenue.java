/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import session.CustomerOrderSessionBeanLocal;

/**
 *
 * @author DEEP
 */
@ManagedBean
@ViewScoped
public class revenue implements Serializable {

    private Date date;
    private Date weekFirstDay;
    private Date weekLastDay;
    private long storeId;
    Calendar cal;
     @EJB
    private CustomerOrderSessionBeanLocal customerOrderSessionBean;

    public revenue() {
          this.date = new Date("11/20/2018");
          storeId=18;
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

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the storeId
     */
    public long getStoreId() {
        return storeId;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @param storeId the storeId to set
     */
    public void setStoreId(long storeId) {
        this.storeId = storeId;
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

    
}
