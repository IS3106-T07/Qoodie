/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.Customer;
import entity.CustomerOrder;
import error.*;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 *
 * @author alex_zy
 */
@Local
public interface CustomerOrderSessionBeanLocal {
    
    public void createCustomerOrder(CustomerOrder c) throws CustomerOrderTypeNotFoundException, OrderDishNotFoundException;
    public CustomerOrder readCustomerOrder(Long cId) throws CustomerOrderNotFoundException;
    public void updateCustomerOrder(CustomerOrder newC) throws CustomerOrderNotFoundException;
    public void updateCustomerOrderNonNullFields(CustomerOrder newC) throws CustomerOrderNotFoundException;
    public void deleteCustomerOrder(CustomerOrder c) throws CustomerOrderNotFoundException, CustomerOrderTypeNotFoundException;

    public List<CustomerOrder> readAllCustomerOrder();
    Customer getCustomerById(Long id);
    
    public double calculateRevenue(Long storeId, Date start, Date end) throws StoreNotFoundException;
    public void payCustomerOrder(CustomerOrder c)
            throws CustomerOrderNotFoundException,
            CustomerOrderTypeNotFoundException,
            CustomerOrderAlreadyPaidException;
    public List<CustomerOrder> getStoreCustomerOrder(Long storeId, Date start, Date end) throws StoreNotFoundException;
}