/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.Canteen;
import entity.CuisineType;
import entity.Customer;
import entity.CustomerOrderType;
import entity.Dish;
import entity.DishType;
import entity.Store;
import entity.UserType;
import error.CanteenNotFoundException;
import error.CuisineTypeNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.DishNotFoundException;
import error.DishTypeNotFoundException;
import error.StoreNotFoundException;
import error.UserTypeNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    //type objects are determined by these string arrays
    private static final String[] dishTypeArr = {"HALAL", "VEGETARIAN"};
    private static final String[] userTypeArr = {"ADMIN", "USER"};
    private static final String[] cuisineTypesArr = {"KOREAN", "JAPANESE", "CHINESE", "WESTERN", "DRINK", "FRUIT", "INDIAN", "MALAY"};
    private static final String[] customerOrderTypesArr = {"IN BASKET", "PAID", "DELIVERED"};
    
    
    @EJB
            CuisineTypeSessionBeanLocal cuisineTypeSessionBeanLocal;
    @EJB
            UserTypeSessionBeanLocal userTypeSessionBeanLocal;
    @EJB
            CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
            StoreSessionBeanLocal storeSessionBeanLocal;
    @EJB
            DishSessionBeanLocal dishSessionBeanLocal;
    @EJB
            DishTypeSessionBeanLocal dishTypeSessionBeanLocal;
    @EJB
            CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionBeanLocal;
    @EJB
            CanteenSessionBeanLocal canteenSessionBeanLocal;
    
    
    @PostConstruct
    public void init(){
        try {
            //init all types first . they are determined by the final arrays
            List<CuisineType> cuisineTypes = cuisineTypeSessionBeanLocal.readAllCuisineType();
            if (cuisineTypes.size() != cuisineTypesArr.length )
                initializeCuisineType();
            
            List<UserType> userTypes = userTypeSessionBeanLocal.readAllUserType();
            if (userTypes.size() != userTypeArr.length )
                initializeUserType();
            
            List<DishType> dishTypes = dishTypeSessionBeanLocal.readAllDishType();
            if (dishTypes.size() != dishTypeArr.length )
                
                initializeDishType();
            
            List<CustomerOrderType> customerOrderTypes = customerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
            if (customerOrderTypes.size() != customerOrderTypesArr.length )
                initializeCustomerOrderType();
            
            initializeUsers();
            initializeStores();
            
        } catch (StoreNotFoundException | DishNotFoundException | CanteenNotFoundException ex) {
            Logger.getLogger(InitializeSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void initializeCuisineType(){
        for (int i = 0; i < cuisineTypesArr.length; i++){
            CuisineType c = new CuisineType();
            c.setName(cuisineTypesArr[i]);
            c.setStores(new ArrayList<>());
            cuisineTypeSessionBeanLocal.createCuisineType(c);
        }
    }
    
    public void initializeUserType(){
        for (int i =0; i<userTypeArr.length;i++){
            UserType u = new UserType();
            u.setName(userTypeArr[i]);
            u.setCustomers(new ArrayList<>());
            userTypeSessionBeanLocal.createUserType(u);
        }
    }
    
    private void initializeDishType() {
        for (int i = 0; i < dishTypeArr.length; i++){
            DishType d = new DishType();
            d.setName(dishTypeArr[i]);
            d.setDishes(new ArrayList<>());
            dishTypeSessionBeanLocal.createDishType(d);
        }
    }
    
    
    private void initializeCustomerOrderType() {
        for (int i = 0; i < customerOrderTypesArr.length; i++){
            CustomerOrderType d = new CustomerOrderType();
            d.setName(customerOrderTypesArr[i]);
            d.setCustomerOrders(new ArrayList<>());
            customerOrderTypeSessionBeanLocal.createCustomerOrderType(d);
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
    
    private void initializeStores() throws StoreNotFoundException, DishNotFoundException, CanteenNotFoundException {
        List<Store> list = storeSessionBeanLocal.readAllStore();
        List<Dish> dishes = initializeDish();
        Canteen c1 = new Canteen();
        c1.setName("finefood");
        c1.setStores(new ArrayList<>());
        canteenSessionBeanLocal.createCanteen(c1);
        Store s = new Store();
        s.setName("Yong Tou Fu");
        s.setPassword("password");
        s.setVendorEmail("vendor1@gmail.com");
        
        
        boolean canInit = true;
        for (Store store : storeSessionBeanLocal.readAllStore()){
            if (store.getVendorEmail().equals("vendor1@gmail.com")){
                s = store;
                canInit = false;
            }
        }
        c1.getStores().add(s);
        s.setCanteen(c1);
        canteenSessionBeanLocal.updateCanteen(c1);
        
        if (canInit) storeSessionBeanLocal.createStore(s);
        
        if (s.getDishes() == null)
            s.setDishes(new ArrayList<>());
        if (s.getDishes().isEmpty()){
            for (Dish d: dishes){
                s.getDishes().add(d);
                d.setStore(s);
                storeSessionBeanLocal.updateStore(s);
                dishSessionBeanLocal.createDish(d);
            }
        }
    }
    
    private List<Dish> initializeDish() {
        List<Dish> dishes = new ArrayList<>();
        
        Dish d = new Dish();
        d.setDescription("leafy green ");
        d.setIsAvailable(true);
        d.setName("Bak Choy");
        d.setPrice(.50);
        dishes.add(d);
        
        Dish d2 = new Dish();
        d2.setDescription("canned ham in slice");
        d2.setIsAvailable(true);
        d2.setName("Ham");
        d2.setPrice(1.0);
        dishes.add(d2);
        
        Dish d3 = new Dish();
        d3.setDescription("crispy and green");
        d3.setIsAvailable(true);
        d3.setName("Seaweed");
        d3.setPrice(.4);
        dishes.add(d3);
        
        Dish d4 = new Dish();
        d4.setDescription("cheesy and white");
        d4.setIsAvailable(true);
        d4.setName("Cheese Tofu");
        d4.setPrice(.4);
        dishes.add(d4);
        
        Dish d5 = new Dish();
        d5.setDescription("the best noodles in the world");
        d5.setIsAvailable(true);
        d5.setName("Maggie noodles");
        d5.setPrice(.4);
        d5.setDishType(null);
        dishes.add(d5);
        
        
        return dishes;
    }
    
    
    
    
    
}
