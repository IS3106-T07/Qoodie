/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CustomerOrder;
import error.CustomerOrderNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class CustomerOrderSessionBean implements CustomerOrderSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;

    @Override
    public void createCustomerOrder(CustomerOrder c) {
        em.persist(c);
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
       c.setCustomer(newC.getCustomer());
       c.setCustomerOrderType(newC.getCustomerOrderType());
       c.setIsAccepted(newC.getIsAccepted());
       c.setOrderDishes(newC.getOrderDishes());
    }

    @Override
    public void deleteCustomerOrder(CustomerOrder c) throws CustomerOrderNotFoundException {
        CustomerOrder co = readCustomerOrder(c.getId());
        em.remove(c);
    }

    @Override
    public List<CustomerOrder> readAllCustomerOrder() {
        return em.createQuery("Select c From CustomerOrder c").getResultList();
    }
}
