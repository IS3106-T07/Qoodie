/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CustomerOrderType;
import error.CustomerOrderTypeNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface CustomerOrderTypeSessionBeanLocal {
    public void createCustomerOrderType(CustomerOrderType c);
    public CustomerOrderType readCustomerOrderType(Long cId) throws CustomerOrderTypeNotFoundException;
    public void updateCustomerOrderType(CustomerOrderType c) throws CustomerOrderTypeNotFoundException;
    public void deleteCustomerOrderType(CustomerOrderType c) throws CustomerOrderTypeNotFoundException;
    
    public List<CustomerOrderType> readAllCustomerOrderType(); //for init bean 
}
