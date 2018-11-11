/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CustomerOrder;
import entity.CustomerOrderType;
import entity.Dish;
import entity.OrderDish;
import entity.Store;
import error.CustomerOrderAlreadyPaidException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.StoreNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
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
    @EJB
    private CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionBeanLocal;
    @EJB
    private StoreSessionBeanLocal storeSessionBeanLocal;

    @Override
    public void createCustomerOrder(CustomerOrder c) throws CustomerOrderTypeNotFoundException {
        c.setCreated(new Date());
        Double price = 0.0;
        for (OrderDish od : c.getOrderDishes()) {
            price += od.getAmount() * od.getDish().getPrice();
        }
        c.setPrice(price);
        List<CustomerOrderType> types = customerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
        for (CustomerOrderType type : types) {
            if (type.getName().contains("IN BASKET")) {
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
        if (c == null) {
            throw new CustomerOrderNotFoundException("customer order not found");
        }
        return c;
    }

    @Override
    public void updateCustomerOrder(CustomerOrder newC) throws CustomerOrderNotFoundException {
        CustomerOrder c = readCustomerOrder(newC.getId());
        c.setLastUpdate(new Date());
        c.setPrice(newC.getPrice());
        c.setCustomer(newC.getCustomer());
        c.setCustomerOrderType(newC.getCustomerOrderType());
        c.setOrderDishes(newC.getOrderDishes());
    }

    @Override
    public void deleteCustomerOrder(CustomerOrder c)
            throws CustomerOrderNotFoundException, CustomerOrderTypeNotFoundException {
        //remove association with type
        CustomerOrderType type = c.getCustomerOrderType();
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
        if (c.getCustomerOrderType().getName().contains("PAID")) {
            throw new CustomerOrderAlreadyPaidException("customer order already paid");
        }
        //remove old association
        List<CustomerOrderType> types = customerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
        CustomerOrderType oldType = c.getCustomerOrderType();
        oldType.getCustomerOrders().remove(c);
        customerOrderTypeSessionBeanLocal.updateCustomerOrderType(oldType);
        //add new association and update 
        for (CustomerOrderType type : types) {
            if (type.getName().contains("PAID")) {
                c.setCustomerOrderType(type);
                type.getCustomerOrders().add(c);
                updateCustomerOrder(c);
                customerOrderTypeSessionBeanLocal.updateCustomerOrderType(type);
                break;
            }
        }
    }

    @Override
    public double calculateRevenue(Long storeId, Date start, Date end) throws StoreNotFoundException {
        double revenue = 0.0;
        Store s = storeSessionBeanLocal.readStore(storeId);
        List<Dish> storeDishes = s.getDishes();
        List<CustomerOrder> storeOrders = new ArrayList<>();

        if (storeDishes == null) {
            return 0.0;
        }
        //get all customer orders belonged to this store
        for (Dish dish : storeDishes) {
            if (dish.getOrderDishes() != null) {
                List<OrderDish> orderdishes = dish.getOrderDishes();
                for (OrderDish orderDish : orderdishes) {
                    storeOrders.add(orderDish.getCustomerOrder());
                }
            }
        }
        //remove duplicates
        Set<CustomerOrder> hs = new HashSet<>();
        hs.addAll(storeOrders);
        storeOrders.clear();
        storeOrders.addAll(hs);
        //filter for dates
        for (CustomerOrder customerOrder : storeOrders) {
            if (customerOrder.getCreated().after(start) || customerOrder.getCreated().equals(start)
                    && (customerOrder.getCreated().before(end)) || customerOrder.getCreated().equals(start)) {
                revenue += customerOrder.getPrice();
            }
        }

        return revenue;
    }
    
    @Override
    public List<CustomerOrder> getStoreCustomerOrder(Long storeId, Date start, Date end) throws StoreNotFoundException {
        
        Store s = storeSessionBeanLocal.readStore(storeId);
        List<Dish> storeDishes = s.getDishes();
        List<CustomerOrder> storeOrders = new ArrayList<>();
        List<CustomerOrder> filteredStoreOrders = new ArrayList<>();
        
        if (storeDishes == null) {
            return filteredStoreOrders;
        }
        //get all customer orders belonged to this store
        for (Dish dish : storeDishes) {
            if (dish.getOrderDishes() != null) {
                List<OrderDish> orderdishes = dish.getOrderDishes();
                for (OrderDish orderDish : orderdishes) {
                    storeOrders.add(orderDish.getCustomerOrder());
                }
            }
        }
        //remove duplicates
        Set<CustomerOrder> hs = new HashSet<>();
        hs.addAll(storeOrders);
        storeOrders.clear();
        storeOrders.addAll(hs);
        //filter for dates
        for (CustomerOrder customerOrder : storeOrders) {
            if (customerOrder.getCreated().after(start) || customerOrder.getCreated().equals(start)
                    && (customerOrder.getCreated().before(end)) || customerOrder.getCreated().equals(start)) {
                filteredStoreOrders.add(customerOrder);
            }
        }

        return filteredStoreOrders;
    }
}
