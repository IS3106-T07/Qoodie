/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.CustomerOrder;
import entity.OrderDish;
import error.CustomerOrderAlreadyPaidException;
import error.CustomerOrderNotFoundException;
import error.CustomerOrderTypeNotFoundException;
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
    
    public void payCustomerOrder(CustomerOrder c)
            throws CustomerOrderNotFoundException,
            CustomerOrderTypeNotFoundException,
            CustomerOrderAlreadyPaidException;
}