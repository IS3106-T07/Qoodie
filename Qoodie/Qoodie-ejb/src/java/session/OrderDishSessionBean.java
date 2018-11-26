/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.OrderDish;
import error.OrderDishNotFoundException;

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
public class OrderDishSessionBean implements OrderDishSessionBeanLocal {

    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;
    
    @Override
    public void createOrderDish(OrderDish d) {
        em.persist(d);
        System.out.println("CREATED ORDER DISH " + d.getId());
    }
    
    @Override
    public List<OrderDish> getStoreOrder(Long id) {
        Query q = em.createQuery("Select o From OrderDish o WHERE o.dish.store.id=:id ");
        q.setParameter("id", id);
        return q.getResultList();
    }

    @Override
    public OrderDish readOrderDish(Long cId) throws OrderDishNotFoundException {
        OrderDish c = em.find(OrderDish.class, cId);
        if (c == null) {
            throw new OrderDishNotFoundException("order dish not found");
        }
        return c;
    }
    
    @Override
    public void updateOrderDish(OrderDish d) throws OrderDishNotFoundException {
        em.merge(d);
//        OrderDish oldC = readOrderDish(d.getId());
        
//        if (d.getAmount() == 0) {
//            em.remove(this);
//        }
//        if (d.getAmount() != null) {
//            oldC.setAmount(d.getAmount());
//        }
//        if (d.getCustomerOrder() != null) {
//            oldC.setCustomerOrder(d.getCustomerOrder());
//        }
//        if (d.getDish() != null) {
//            oldC.setDish(d.getDish());
//        }
    }
    
    @Override
    public void deleteOrderDish(OrderDish d) throws OrderDishNotFoundException {
        em.remove(readOrderDish(d.getId()));
    }
    
    @Override
    public List<OrderDish> readAllOrderDish() {
        return (em.createQuery("SELECT f FROM OrderDish f").getResultList());
    }
    
    public void persist(Object object) {
        em.persist(object);
    }
    
}
