/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.*;
import enums.UserType;
import error.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    CustomerOrderSessionBeanLocal customerOrderSessionBean;
    @EJB
    OrderDishSessionBeanLocal orderDishSessionBean;
    @EJB
    CanteenSessionBeanLocal canteenSessionBeanLocal;
    @EJB
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal;

    @PostConstruct
    public void init() {
        try {
            //init all types first . they are determined by the final arrays
            List<CuisineType> cuisineTypes = cuisineTypeSessionBeanLocal.readAllCuisineType();
            if (cuisineTypes.size() != cuisineTypesArr.length) {
                initializeCuisineType();
            }

            List<DishType> dishTypes = dishTypeSessionBeanLocal.readAllDishType();
            if (dishTypes.size() != dishTypeArr.length) {
                initializeDishType();
            }

            List<CustomerOrderType> customerOrderTypes = customerOrderTypeSessionBeanLocal.readAllCustomerOrderType();
            if (customerOrderTypes.size() != customerOrderTypesArr.length) {
                initializeCustomerOrderType();
            }

            initializeCanteen();
            initializeStores();
            initializeUsers();
            //initializeOrderDish();
            initializeCustomerOrder();

        } catch (StoreNotFoundException | DishNotFoundException | CanteenNotFoundException ex) {

            Logger.getLogger(InitializeSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void initializeCuisineType() {
        for (int i = 0; i < cuisineTypesArr.length; i++) {
            CuisineType c = new CuisineType();
            c.setName(cuisineTypesArr[i]);
            c.setStores(new ArrayList<>());
            cuisineTypeSessionBeanLocal.createCuisineType(c);
        }
    }
/*
    public void initializeOrderDish() {
        try {
            OrderDish o1 = new OrderDish();
            o1.setAmount(4);

            o1.setDish(dishSessionBeanLocal.readDish(21L));
            orderDishSessionBean.createOrderDish(o1);

            OrderDish o2 = new OrderDish();
            o2.setAmount(5);
// 
            o2.setDish(dishSessionBeanLocal.readDish(20L));
            orderDishSessionBean.createOrderDish(o2);

            OrderDish o3 = new OrderDish();
            o3.setAmount(1);
//       
            o3.setDish(dishSessionBeanLocal.readDish(22L));
            orderDishSessionBean.createOrderDish(o3);

            OrderDish o4 = new OrderDish();
            o4.setAmount(6);
//    
            o4.setDish(dishSessionBeanLocal.readDish(19L));
            orderDishSessionBean.createOrderDish(o4);

        } catch (Exception e) {
        }
    }
    */

    public void initializeCustomerOrder() {
        try {
            CustomerOrder c = new CustomerOrder();
            c.setCustomer(customerSessionBeanLocal.readCustomer(16L));
            c.setCustomerOrderType(customerOrderTypeSessionBeanLocal.readCustomerOrderType(15L));

            ArrayList<OrderDish> od = new ArrayList<OrderDish>();
            od.add(orderDishSessionBean.readOrderDish(24L));
            od.add(orderDishSessionBean.readOrderDish(26L));
            c.setOrderDishes(od);
            c.setCreated(new Date());
            c.setLastUpdate(new Date());
            customerOrderSessionBean.createCustomerOrder(c);

            c = new CustomerOrder();
            c.setCustomer(customerSessionBeanLocal.readCustomer(17L));
            c.setCustomerOrderType(customerOrderTypeSessionBeanLocal.readCustomerOrderType(14L));

            od = new ArrayList<OrderDish>();
            od.add(orderDishSessionBean.readOrderDish(24L));
            od.add(orderDishSessionBean.readOrderDish(25L));
            c.setOrderDishes(od);
            customerOrderSessionBean.createCustomerOrder(c);

            List<OrderDish> o1 = orderDishSessionBean.readAllOrderDish();
            o1.get(0).setCustomerOrder(customerOrderSessionBean.readCustomerOrder(28L));
            o1.get(1).setCustomerOrder(customerOrderSessionBean.readCustomerOrder(29L));
            o1.get(2).setCustomerOrder(customerOrderSessionBean.readCustomerOrder(28L));
            o1.get(3).setCustomerOrder(customerOrderSessionBean.readCustomerOrder(29L));
        } catch (Exception e) {

            System.out.println("ERROR " + e.toString());
        }
    }

    private void initializeDishType() {
        for (int i = 0; i < dishTypeArr.length; i++) {
            DishType d = new DishType();
            d.setName(dishTypeArr[i]);
            d.setDishes(new ArrayList<>());
            dishTypeSessionBeanLocal.createDishType(d);
        }
    }

    private void initializeCustomerOrderType() {
        for (int i = 0; i < customerOrderTypesArr.length; i++) {
            CustomerOrderType d = new CustomerOrderType();
            d.setName(customerOrderTypesArr[i]);
            d.setCustomerOrders(new ArrayList<>());
            customerOrderTypeSessionBeanLocal.createCustomerOrderType(d);
        }
    }

    //initialise 2 users: Alice and Bob
    public void initializeUsers() {

        if (customerSessionBeanLocal.readCustomerByEmail("alice@gmail.com").isEmpty()) {
            Customer c = new Customer();
            c.setAddress("41 Sungei Kadut Loop S 729509, Singapore");
            c.setEmail("alice@gmail.com");
            c.setName("Alice");
            c.setPassword("password");
            c.setPhone("88888888");
            c.setUserType(UserType.STUDENT);
            customerSessionBeanLocal.createCustomer(c);
            initialiseCustomerOrderForUser(c);
        }
        if (customerSessionBeanLocal.readCustomerByEmail("bob@gmail.com").isEmpty()) {
            Customer c = new Customer();
            c.setName("Bob");
            c.setEmail("bob@gmail.com");
            c.setPhone("66666666");
            c.setAddress("31 Sungei Kadut Loop S 729509, Singapore");
            c.setPassword("password");
            c.setUserType(UserType.ADMIN);
            customerSessionBeanLocal.createCustomer(c);
            initialiseCustomerOrderForUser(c);
        }

    }

    private void initializeCanteen() {
        List<Canteen> canteens = new ArrayList<>();

        Canteen fineFood = new Canteen();
        fineFood.setName("FineFood");
        fineFood.setStores(new ArrayList<>());
        canteens.add(fineFood);
        Canteen foodClick = new Canteen();
        foodClick.setName("FoodClick");
        foodClick.setStores(new ArrayList<>());
        canteens.add(foodClick);
        Canteen deck = new Canteen();
        deck.setName("Deck");
        deck.setStores(new ArrayList<>());
        canteens.add(deck);
        for (Canteen c : canteens) {
            if (canteenSessionBeanLocal.readCanteenByName(c.getName()).isEmpty()) {
                canteenSessionBeanLocal.createCanteen(c);
                System.out.printf("***** canteen (name = %s) created\n", c.getName());
            }
        }
    }

    private void initializeStores() throws StoreNotFoundException, DishNotFoundException, CanteenNotFoundException {

        if (!canteenSessionBeanLocal.readCanteenByName("FineFood").isEmpty()) {
            Canteen fineFood = canteenSessionBeanLocal.readCanteenByName("FineFood").get(0);
            if (storeSessionBeanLocal.readStoreByEmail("vendor1@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Yong Tou Fu");

                Customer c = new Customer();
                c.setName("Christine");
                c.setEmail("christine@gmail.com");
                c.setPhone("66666666");
                c.setAddress("31 Jalan Besah Road 985678, Singapore");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);

                s.setCanteen(fineFood);
                List<Dish> dishes = new ArrayList<>();
                //create a dummy dish in the store 
                Dish dish = new Dish("Bak Choy", 2 * Math.random(), "high fiber and full of vitamin");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Sliced Ham", 3 * Math.random(), "canned. size: half of a circle");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Cheese tofu", 3 * Math.random(), "Must try");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("fish ball", 3 * Math.random(), "made with real fish");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("crab  stick", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Tau pok", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Beancurd Sheet", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Tau Pok with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Green Chilli with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Ladies Finger with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Brinjal with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Bittergourd with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Red Chilli with Fish Paste ", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Egg Tofu", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("kway teow", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("yello noodles", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("been hoon", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("vermicilli", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("hor fun", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                s.setDishes(dishes);

                storeSessionBeanLocal.createStore(s);
                fineFood.getStores().add(s);
            }
            if (storeSessionBeanLocal.readStoreByEmail("finefoodgc@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Gong Cha");
                Customer c = new Customer();
                c.setEmail("finefoodgc@gmail.com");
                c.setPassword("password");
                customerSessionBeanLocal.createCustomer(c);
                c.setUserType(UserType.VENDOR);
                s.setVendor(c);

                s.setCanteen(fineFood);
                List<Dish> dishes = new ArrayList<>();

                //create a dummy dish in the store 
                Dish dish = new Dish("Earl Grey Milk Tea", 4 * Math.random(), "default: 50% sugar, regular ice");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Earl Grey Milk Tea", 4 * Math.random(), "default: 50% sugar, regular ice");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Black Milk Tea", 4 * Math.random(), "default: 50% sugar, regular ice");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Taro  Milk Tea", 4 * Math.random(), "default: 50% sugar, regular ice");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Milk foam green Tea", 4 * Math.random(), "default: 50% sugar, regular ice");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Milk Foam winter melon", 4 * Math.random(), "default: 50% sugar, regular ice");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("0% sugar", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("20% sugar", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("50% sugar", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("75% sugar", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("0% ice", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("20% ice", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("50% ice", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("75% ice", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("warm drink", 0.0, "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                s.setDishes(dishes);

                storeSessionBeanLocal.createStore(s);
                fineFood.getStores().add(s);
            }
            if (storeSessionBeanLocal.readStoreByEmail("finefoodk@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Korean and Japanese");
                Customer c = new Customer();
                c.setEmail("finefoodk@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(fineFood);
                List<Dish> dishes = new ArrayList<>();
                //create a dummy dish in the store                
                Dish dish = new Dish("Chicken Kimchi Soup", 7 * Math.random(), "it also has bak choy and vermecilli");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store                
                dish = new Dish("spicy marinated chicken", 7 * Math.random(), "with rice and miso soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store                
                dish = new Dish("spicy marinated beef", 7 * Math.random(), "with rice and miso soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store                
                dish = new Dish("spicy marinated pork", 7 * Math.random(), "with rice and miso soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store                
                dish = new Dish("Saba fish", 7 * Math.random(), "with rice and miso soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                s.setDishes(dishes);
                storeSessionBeanLocal.createStore(s);
                fineFood.getStores().add(s);
            }
            canteenSessionBeanLocal.updateCanteen(fineFood);
        }
        //initialize stores for deck
        if (!canteenSessionBeanLocal.readCanteenByName("Deck").isEmpty()) {
            Canteen deck = canteenSessionBeanLocal.readCanteenByName("Deck").get(0);
            if (storeSessionBeanLocal.readStoreByEmail("deckytf@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Yong Tou Fu");
                Customer c = new Customer();
                c.setEmail("deckytf@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(deck);
                List<Dish> dishes = new ArrayList<>();
                //create a dummy dish in the store 
                Dish dish = new Dish("Fish Ball", 2 * Math.random(), "Made of real fish!");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("Sliced Ham", 3 * Math.random(), "canned. size: half of a circle");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Cheese tofu", 3 * Math.random(), "Must try");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("crab  stick", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Tau pok", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Beancurd Sheet", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Tau Pok with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Green Chilli with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Ladies Finger with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Brinjal with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Bittergourd with Fish Paste", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Red Chilli with Fish Paste ", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Egg Tofu", 3 * Math.random(), "made with real crab");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("kway teow", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("yello noodles", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("been hoon", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("vermicilli", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("hor fun", 1.0, "noodles");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                s.setDishes(dishes);

                storeSessionBeanLocal.createStore(s);
                deck.getStores().add(s);
            }
            if (storeSessionBeanLocal.readStoreByEmail("deckcp@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Claypot");
                Customer c = new Customer();
                c.setEmail("deckcp@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(deck);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("braised chicken with potato", 7 * Math.random(), "traditional chinese style");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                //create a dummy dish in the store 
                dish = new Dish("clay pot sepcial", 7 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Claypot chicken rice with mushroom",  7 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Claypot chicken rice with egg",  7 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Claypot Yee Mee Soup with egg",  7 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                
                s.setDishes(dishes);

                deck.getStores().add(s);
                storeSessionBeanLocal.createStore(s);
            }
            if (storeSessionBeanLocal.readStoreByEmail("deckcr@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Chicken Rice");
                Customer c = new Customer();
                c.setEmail("deckcr@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(deck);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Roasted Chicken Rice", 5 * Math.random(), "popular item");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                
                 //create a dummy dish in the store 
                dish = new Dish("Chicken rice with veg", 7 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Chicken rice set",  7 * Math.random(), "with half an egg and soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Roasted Chicken rice set",  7 * Math.random(), "with half an egg and soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("Chicken Noodles",  6 * Math.random(), "dry noodles with soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                
                s.setDishes(dishes);

                deck.getStores().add(s);
                storeSessionBeanLocal.createStore(s);
            }
            canteenSessionBeanLocal.updateCanteen(deck);
        }
        //initialise the stores for FoodClick
        if (!canteenSessionBeanLocal.readCanteenByName("FoodClick").isEmpty()) {
            Canteen foodClick = canteenSessionBeanLocal.readCanteenByName("FoodClick").get(0);
            if (storeSessionBeanLocal.readStoreByEmail("foodclickbm@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Ban Mian");
                Customer c = new Customer();
                c.setEmail("foodclickbm@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(foodClick);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Fish and Mushroom ban mian", 6 * Math.random(), "you can choose in soup or dried");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                
                 //create a dummy dish in the store 
                dish = new Dish("singature dry chilli ban mian", 7 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("clam pan ban mian",  7 * Math.random(), "with half an egg and soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("homemade fishball soup",  7 * Math.random(), "with half an egg and soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("meatball soup",  6 * Math.random(), "dry noodles with soup");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                s.setDishes(dishes);

                storeSessionBeanLocal.createStore(s);
                foodClick.getStores().add(s);
            }

            if (storeSessionBeanLocal.readStoreByEmail("foodclickfj@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Fruit and Juice");
                Customer c = new Customer();
                c.setEmail("foodclickfj@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(foodClick);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("apple juice", 1 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                
                 //create a dummy dish in the store 
                dish = new Dish("pear juice", 1 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("carrot juice",  1 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("cucumber juice",  1 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("banana milk shake",  1 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                
                s.setDishes(dishes);
                storeSessionBeanLocal.createStore(s);
                foodClick.getStores().add(s);
                storeSessionBeanLocal.updateStore(s);
            }

            if (storeSessionBeanLocal.readStoreByEmail("foodclicka@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Astons");
                Customer c = new Customer();
                c.setEmail("foodclicka@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(foodClick);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("fiery chicken", 20 * Math.random(), "actually very spicy");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                
                 //create a dummy dish in the store 
                dish = new Dish("chargrill chicken", 20 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("black pepper chicken",  20 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("teriyaki chicken",  20 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);

                //create a dummy dish in the store 
                dish = new Dish("new york strip",  20 * Math.random(), "");
                dishSessionBeanLocal.createDish(dish);
                dishes.add(dish);
                dish.setStore(s);
                
                
                s.setDishes(dishes);

                storeSessionBeanLocal.createStore(s);
                foodClick.getStores().add(s);
            }
            canteenSessionBeanLocal.updateCanteen(foodClick);
        }

    }

    private List<Dish> initializeDish() {
        List<Dish> dishes = new ArrayList<>();

        Dish d = new Dish();
        d.setDescription("leafy green");
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

    //input: a user
    //description: this method creates a customer order for input user(store -> item -> orderitem -> custoemrorder)
    private void initialiseCustomerOrderForUser(Customer c) {
        try {
            Customer customer = customerSessionBeanLocal.readCustomer(c.getId());
            List<Store> storeList = storeSessionBeanLocal.readAllStore();
            Store store = storeList.get((int) (Math.random() * storeList.size()));
            if (!store.getDishes().isEmpty()) {
                OrderDish orderDish = new OrderDish();
                orderDish.setDish(store.getDishes().get(0));
                Dish dish = orderDish.getDish();
                dish.setOrderDishes(new ArrayList<>());
                dish.getOrderDishes().add(orderDish);
                dishSessionBeanLocal.updateDish(dish);
                orderDish.setAmount(1 + (int) (Math.random() * 10));
                orderDishSessionBean.createOrderDish(orderDish);
                CustomerOrder customerOrder = new CustomerOrder();
                customerOrder.setCustomer(customer);
                List<OrderDish> odList = new ArrayList<>();
                odList.add(orderDish);
                customerOrder.setOrderDishes(odList);
                customerOrderSessionBeanLocal.createCustomerOrder(customerOrder);
                customer.getCustomerOrders().add(customerOrder);
                customerSessionBeanLocal.updateCustoemr(c);
            }
        } catch (CustomerNotFoundException | CustomerOrderTypeNotFoundException | OrderDishNotFoundException | DishNotFoundException ex) {
            Logger.getLogger(InitializeSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
