/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.CustomerOrder;
import entity.OrderDish;
import entity.CustomerOrderType;
import error.CustomerOrderAlreadyPaidException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author alex_zy
 */
@Stateless
public class CustomerOrderSessionBean implements CustomerOrderSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;
    @EJB
    private CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionBeanLocal;
    
    @Override
    public void createCustomerOrder(CustomerOrder c) throws CustomerOrderTypeNotFoundException {
        c.setCreated(new Date());
        List<CustomerOrderType> types = customerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
        for (CustomerOrderType type : types){
            if (type.getName().contains("IN BASKET")){
                c.setCustomerOrderType(type);
                type.getCustomerOrders().add(c);
                customerOrderTypeSessionBeanLocal.updateCustomerOrderType(type);
                break;
            }
        }
        em.persist(c);
        em.flush();
    }
    
    @Override
    public CustomerOrder readCustomerOrder(Long cId) throws CustomerOrderNotFoundException {
        CustomerOrder c = em.find(CustomerOrder.class, cId);
        if (c==null) throw new CustomerOrderNotFoundException("customer order not found");
        return c;
    }
    
    
    
    @Override
    public void updateCustomerOrder(CustomerOrder newC) throws CustomerOrderNotFoundException {
        CustomerOrder c = readCustomerOrder(newC.getId());
        c.setLastUpdate(new Date());
        c.setCustomer(newC.getCustomer());
        c.setCustomerOrderType(newC.getCustomerOrderType());
        c.setIsAccepted(newC.getIsAccepted());
        c.setOrderDishes(newC.getOrderDishes());
    }
    
    @Override
    public void deleteCustomerOrder(CustomerOrder c)
            throws CustomerOrderNotFoundException, CustomerOrderTypeNotFoundException {
        //remove association with type
        CustomerOrderType type= c.getCustomerOrderType();
        type.getCustomerOrders().remove(c);
        customerOrderTypeSessionBeanLocal.updateCustomerOrderType(type);
        c.setCustomerOrderType(null);
        em.remove(readCustomerOrder(c.getId()));
    }
    
    @Override
    public List<CustomerOrder> readAllCustomerOrder() {
        return em.createQuery("Select c From CustomerOrder c").getResultList();
    }
    
    @Override
    public void payCustomerOrder(CustomerOrder c) throws CustomerOrderNotFoundException, CustomerOrderTypeNotFoundException, CustomerOrderAlreadyPaidException {
        //throw exception if the order is already paid
        if (c.getCustomerOrderType().getName().contains("PAID"))
            throw new CustomerOrderAlreadyPaidException("customer order already paid");
        //remove old association
        List<CustomerOrderType> types = customerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
        CustomerOrderType oldType = c.getCustomerOrderType();
        oldType.getCustomerOrders().remove(c);
        customerOrderTypeSessionBeanLocal.updateCustomerOrderType(oldType);
        //add new association and update 
        for (CustomerOrderType type : types){
            if (type.getName().contains("PAID")){
                c.setCustomerOrderType(type);
                type.getCustomerOrders().add(c);
                updateCustomerOrder(c);
                customerOrderTypeSessionBeanLocal.updateCustomerOrderType(type);
                break;
            }
        }
    }
}
