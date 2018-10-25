/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.CuisineType;
import entity.Customer;
import entity.Store;
import entity.UserType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author alex_zy
 */
@Startup
@Singleton
@LocalBean
public class InitializeSessionBean {
    @EJB
            CuisineTypeSessionBeanLocal cuisineTypeSessionBeanLocal;
    @EJB
            UserTypeSessionBeanLocal userTypeSessionBeanLocal;
    @EJB
            CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
            StoreSessionBeanLocal storeSessionBeanLocal;
    @PostConstruct
    public void init(){
        initializeCuisineType();
        initializeUserType();
        initializeUsers();
        initializeStores();
    }
    
    public void initializeCuisineType(){
        List<CuisineType> list = cuisineTypeSessionBeanLocal.readAllCuisineType();
        if (list.isEmpty()){
            String[] names = {"KOREAN", "JAPANESE", "CHINESE", "WESTERN", "DRINK", "FRUIT", "INDIAN", "MALAY"};
            for (int i = 0; i < names.length; i++){
                CuisineType c = new CuisineType();
                c.setName(names[i]);
                c.setStores(new ArrayList<>());
                cuisineTypeSessionBeanLocal.createCuisineType(c);
            }
        }
    }
    public void initializeUserType(){
        List<UserType> list = userTypeSessionBeanLocal.readAllUserType();
        if (list.isEmpty()){
            String[] names = {"MEMBER", "ADMIN"};
            for (int i =0; i<names.length;i++){
                UserType u = new UserType();
                u.setName(names[i]);
                userTypeSessionBeanLocal.createUserType(u);
            }
        }
    }
    
    //initialise 2 users: Alice and Bob
    public void initializeUsers(){
        List<Customer> list = customerSessionBeanLocal.readAllCustomer();
        if (list.isEmpty()){
            Customer c = new Customer();
            c.setAddress("41 Sungei Kadut Loop S 729509, Singapore");
            c.setEmail("alice@gmail.com");
            c.setName("Alice");
            c.setPassword("password");
            c.setPhone("88888888");
            c.setUserType(userTypeSessionBeanLocal.readAllUserType().get(0));
            customerSessionBeanLocal.createCustomer(c);
            c = new Customer();
            c.setName("Bob");
            c.setEmail("bob@gmail.com");
            c.setUserType(userTypeSessionBeanLocal.readAllUserType().get(1));
            c.setPhone("66666666");
            c.setAddress("31 Sungei Kadut Loop S 729509, Singapore");
            c.setPassword("password");
            customerSessionBeanLocal.createCustomer(c);
        }
    }
    
    private void initializeStores() {
        List<Store> list = storeSessionBeanLocal.readAllStore();
        if (list.isEmpty()){
            Store s = new Store();
            s.setDishes(new ArrayList<>());
            s.setName("Yong Tou Fu"); 
            s.setPassword("password");
            s.setVendorEmail("vendor1@gmail.com");
            storeSessionBeanLocal.createStore(s);
            
            s = new Store();
            s.setDishes(new ArrayList<>());
            s.setName("Western"); 
            s.setPassword("password");
            s.setVendorEmail("vendor2@gmail.com");
            storeSessionBeanLocal.createStore(s);
        }
    }
}
