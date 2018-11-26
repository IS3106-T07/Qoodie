/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CustomerOrderType;
import error.CustomerOrderTypeNotFoundException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author alex_zy
 */
@Stateless
public class CustomerOrderTypeSessionBean implements CustomerOrderTypeSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;

    @Override
    public void createCustomerOrderType(CustomerOrderType c) {
        em.persist(c);
        System.out.println("CREATED CUSTOMER TYPE " + c.getName());
    }

    @Override
    public CustomerOrderType readCustomerOrderType(Long cId) throws CustomerOrderTypeNotFoundException {
        CustomerOrderType c = em.find(CustomerOrderType.class, cId);
        if (c==null) throw new CustomerOrderTypeNotFoundException("custoemr order type not found");
        return c;
    }

    @Override
    public void updateCustomerOrderType(CustomerOrderType c) throws CustomerOrderTypeNotFoundException {
//        CustomerOrderType OldC = readCustomerOrderType(c.getId());
//        OldC.setCustomerOrders(c.getCustomerOrders());
//        OldC.setName(c.getName());
        em.merge(c);
    }

    @Override
    public void deleteCustomerOrderType(CustomerOrderType c) throws CustomerOrderTypeNotFoundException {
        em.remove(readCustomerOrderType(c.getId()));
    }
    
    @Override
    public List<CustomerOrderType> readAllCustomerOrderType() {
        return em.createQuery("SELECT c From CustomerOrderType c").getResultList();
    }

    @Override
    public List<CustomerOrderType> readCustomerOrderTypeByName(String name) {
        Query query = em.createQuery("SELECT c From CustomerOrderType c WHERE LOWER(c.name) = :name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }
}
