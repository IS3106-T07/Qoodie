/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.CustomerOrder;
import error.CustomerOrderAlreadyPaidException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.StoreNotFoundException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface CustomerOrderSessionBeanLocal {
    
    public void createCustomerOrder(CustomerOrder c) throws CustomerOrderTypeNotFoundException;
    public CustomerOrder readCustomerOrder(Long cId) throws CustomerOrderNotFoundException;
    public void updateCustomerOrder(CustomerOrder newC) throws CustomerOrderNotFoundException;
    public void deleteCustomerOrder(CustomerOrder c) throws CustomerOrderNotFoundException, CustomerOrderTypeNotFoundException;
    
    public List<CustomerOrder> readAllCustomerOrder();
    
    public double calculateRevenue(Long storeId, Date start, Date end) throws StoreNotFoundException;
    public void payCustomerOrder(CustomerOrder c)
            throws CustomerOrderNotFoundException,
            CustomerOrderTypeNotFoundException,
            CustomerOrderAlreadyPaidException;
}