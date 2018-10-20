/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Customer;
import error.CustomerNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface CustomerSessionBeanLocal {
    public void createCustomer(Customer c);
    public Customer readCustomer(Long cId) throws CustomerNotFoundException;
    public void updateCustoemr(Customer c) throws CustomerNotFoundException;
    public void deleteCustomer(Customer c)throws CustomerNotFoundException;
    
    public List<Customer> readAllCustomer();//for admin
}
