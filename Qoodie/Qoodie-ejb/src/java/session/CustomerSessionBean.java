/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.Customer;
import entity.CustomerOrder;
import error.CustomerNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;
    
    //@EJB
    //CustomerOrderSessionBean customerOrderSessionBean;
    @Override
    public void createCustomer(Customer c) {
        c.setCreated(new Date());
        c.setCustomerOrders(new ArrayList<>());
        em.persist(c);
    }
    
    @Override
    public Customer readCustomer(Long cId) throws CustomerNotFoundException {
        Customer c = em.find(Customer.class, cId);
        if (c == null) throw new CustomerNotFoundException("customer not found");
        return c;
    }
    
    @Override
    public void updateCustoemr(Customer c) throws CustomerNotFoundException {
        Customer oldC = readCustomer(c.getId());
        
        oldC.setAddress(c.getAddress()!=null?c.getAddress():oldC.getAddress());
        oldC.setCustomerOrders(c.getCustomerOrders()!=null?c.getCustomerOrders():oldC.getCustomerOrders());
        oldC.setEmail(c.getEmail()!=null?c.getEmail():oldC.getEmail());
        oldC.setIsActive(c.getIsActive()!=null?c.getIsActive():oldC.getIsActive());
        oldC.setName(c.getName()!=null?c.getName():oldC.getName());
        oldC.setPassword(c.getPassword()!=null?c.getPassword():oldC.getPassword());
        oldC.setPhone(c.getPhone()!=null?c.getPhone():oldC.getPhone());
        oldC.setUserType(c.getUserType()!=null?c.getUserType():oldC.getUserType());
    }
    
    @Override
    public void deleteCustomer(Customer c) throws CustomerNotFoundException {
        Customer customer = readCustomer(c.getId());
        List <CustomerOrder> customerOrders = customer.getCustomerOrders();
        // cascade delete all customer orders associated
        for (CustomerOrder co : customerOrders){
            //customerOrderSessionBean.deleteCustomerOrders(co); TODO: implement this method
        }
        em.remove(c);
    }
    
    @Override
    public List<Customer> readAllCustomer() {
        return em.createQuery("SELECT c FROM Customer c").getResultList();
    }
    
    @Override
    public List<Customer> readCustomerByEmail(String email) {
        List<Customer> returned =  new ArrayList<>();
        List<Customer> all = readAllCustomer();
        for (Customer c : all){
            if (c.getEmail().toLowerCase().equals(email.toLowerCase())){
                returned.add(c);
            }
        }
        return returned;
    }
    
    
}
