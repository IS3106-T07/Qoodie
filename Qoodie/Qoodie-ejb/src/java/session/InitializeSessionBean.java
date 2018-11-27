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
import java.util.Arrays;
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
    private static final String[] customerOrderTypesArr = {"IN BASKET", "PAID", "READY", "DELIVERED"};

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
//            initializeCustomerOrder();

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
        Canteen foodClique = new Canteen();
        foodClique.setName("FoodClique");
        foodClique.setStores(new ArrayList<>());
        canteens.add(foodClique);
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
            List<Store> stores = fineFood.getStores();
            stores = stores == null ? new ArrayList<>() : stores;
            if (storeSessionBeanLocal.readStoreByEmail("christine@gmail.com").isEmpty()) {
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
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes, "Bak Choy", 2, "high fiber and full of vitamin");
                initDish(s, dishes, "Sliced Ham", 3, "canned. size: half of a circle");
                initDish(s, dishes, "Cheese tofu", 3, "Must try");
                initDish(s, dishes, "fish ball", 3, "made with real fish");
                initDish(s, dishes, "crab stick", 3, "made with real crab");
                initDish(s, dishes, "Tau pok", 3, "made with real crab");
                initDish(s, dishes, "Beancurd Sheet", 3, "made with real crab");
                initDish(s, dishes, "Tau Pok with Fish Paste", 3, "made with real crab");
                initDish(s, dishes, "Green Chilli with Fish Paste", 3, "made with real crab");
                initDish(s, dishes, "Ladies Finger with Fish Paste", 3, "made with real crab");
                initDish(s, dishes, "Brinjal with Fish Paste", 3, "made with real crab");
                initDish(s, dishes, "Bittergourd with Fish Paste", 3, "made with real crab");
                initDish(s, dishes, "Red Chilli with Fish Paste ", 3, "made with real crab");
                initDish(s, dishes, "Egg Tofu", 3, "made with real crab");
                initDish(s, dishes, "kway teow", 1, "noodles");
                initDish(s, dishes, "yellow noodles", 1, "noodles");
                initDish(s, dishes, "bee hoon", 1, "noodles");
                initDish(s, dishes, "vermicelli", 1, "noodles");
                initDish(s, dishes, "hor fun", 1, "noodles");
                s.setDishes(dishes);

                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                fineFood.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(fineFood);
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
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes, "Earl Grey Milk Tea", 4, "default: 50% sugar, regular ice");
 
                initDish(s, dishes, "Earl Grey Milk Tea", 4, "default: 50% sugar, regular ice");
 
                initDish(s, dishes, "Black Milk Tea", 4, "default: 50% sugar, regular ice");
 
                initDish(s, dishes, "Taro  Milk Tea", 4, "default: 50% sugar, regular ice");
 
                initDish(s, dishes, "Milk foam green Tea", 4, "default: 50% sugar, regular ice");
 
                initDish(s, dishes, "Milk Foam winter melon", 4, "default: 50% sugar, regular ice");

                initDish(s, dishes,"0% sugar", 0,"");
                initDish(s, dishes,"20% sugar", 0,"");
                initDish(s, dishes,"50% sugar", 0,"");
                initDish(s, dishes,"75% sugar", 0,"");

                initDish(s, dishes,"0% ice", 0,"");
                initDish(s, dishes,"20% ice", 0,"");
                initDish(s, dishes,"50% ice", 0,"");
                initDish(s, dishes,"75% ice", 0,"");

                initDish(s, dishes,"warm drink", 0,"");

                s.setDishes(dishes);

                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                fineFood.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(fineFood);
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
                storeSessionBeanLocal.createStore(s);
                List<Dish> dishes = new ArrayList<>();
                initDish(s, dishes, "chicken kimchi soup", 7, "it also has bak choy and vermecelli");
                initDish(s, dishes, "spicy marinated chicken", 7, "with rice and miso soup");                
                initDish(s, dishes, "spicy marinated beef", 7, "with rice and miso soup");                
                initDish(s, dishes, "spicy marinated pork", 7, "with rice and miso soup");                
                initDish(s, dishes, "Saba fish", 7, "with rice and miso soup");
                System.out.println("DISHES");
                System.out.println(dishes);
                s.setDishes(dishes);
                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                fineFood.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(fineFood);
            }
            canteenSessionBeanLocal.updateCanteen(fineFood);
        }
        //initialize stores for deck
        if (!canteenSessionBeanLocal.readCanteenByName("Deck").isEmpty()) {
            Canteen deck = canteenSessionBeanLocal.readCanteenByName("Deck").get(0);
            List<Store> stores = deck.getStores();
            stores = stores == null ? new ArrayList<>() : stores;
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
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes,"Fish Ball", 0,"Made of real fish!");

                initDish(s, dishes, "Sliced Ham", 3, "canned. size: half of a circle");
 
                initDish(s, dishes, "Cheese tofu", 3, "Must try");
 
                initDish(s, dishes, "crab  stick", 3, "made with real crab");
 
                initDish(s, dishes, "Tau pok", 3, "made with real crab");
 
                initDish(s, dishes, "Beancurd Sheet", 3, "made with real crab");
 
                initDish(s, dishes, "Tau Pok with Fish Paste", 3, "made with real crab");
 
                initDish(s, dishes, "Green Chilli with Fish Paste", 3, "made with real crab");
 
                initDish(s, dishes, "Ladies Finger with Fish Paste", 3, "made with real crab");
 
                initDish(s, dishes, "Brinjal with Fish Paste", 3, "made with real crab");
 
                initDish(s, dishes, "Bittergourd with Fish Paste", 3, "made with real crab");
 
                initDish(s, dishes, "Red Chilli with Fish Paste ", 3, "made with real crab");
 
                initDish(s, dishes, "Egg Tofu", 3, "made with real crab");

                initDish(s, dishes,"kway Teow", 1,"noodles");

                initDish(s, dishes,"Yellow Noodles", 0,"noodles");

                initDish(s, dishes,"Bee Hoon", 0,"noodles");

                initDish(s, dishes,"Vermicelli", 0,"noodles");

                initDish(s, dishes,"Hor Fun", 0,"noodles");

                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                deck.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(deck);
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
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes,"braised chicken with potato", 7,"traditional chinese style");

                initDish(s, dishes, "clay pot sepcial", 7, "");
 
                initDish(s, dishes, "Claypot chicken rice with mushroom", 7, "");
 
                initDish(s, dishes, "Claypot chicken rice with egg", 7, "");
 
                initDish(s, dishes, "Claypot Yee Mee Soup with egg", 7, "");
                s.setDishes(dishes);
                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                deck.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(deck);
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
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes,"Roasted Chicken Rice", 5,"popular item");
                
                 //create a dummy dish in the store 
                initDish(s, dishes, "Chicken rice with veg", 7, "");
 
                initDish(s, dishes, "Chicken rice set", 7, "with half an egg and soup");
 
                initDish(s, dishes, "Roasted Chicken rice set", 7, "with half an egg and soup");
 
                initDish(s, dishes, "Chicken Noodles", 6, "dry noodles with soup");
                s.setDishes(dishes);
                storeSessionBeanLocal.updateStore(s);

                stores.add(s);
                deck.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(deck);
            }
            canteenSessionBeanLocal.updateCanteen(deck);
        }
        //initialise the stores for FoodClique
        if (!canteenSessionBeanLocal.readCanteenByName("FoodClique").isEmpty()) {
            Canteen foodClique = canteenSessionBeanLocal.readCanteenByName("FoodClique").get(0);
            List<Store> stores = foodClique.getStores();
            stores = stores == null ? new ArrayList<>() : stores;
            if (storeSessionBeanLocal.readStoreByEmail("foodcliquebm@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Ban Mian");
                Customer c = new Customer();
                c.setEmail("foodcliquebm@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(foodClique);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes,"Fish and Mushroom ban mian", 6,"you can choose in soup or dried");
                
                 //create a dummy dish in the store 
                initDish(s, dishes, "singature dry chilli ban mian", 7, "");
 
                initDish(s, dishes, "clam pan ban mian", 7, "with half an egg and soup");
 
                initDish(s, dishes, "homemade fishball soup", 7, "with half an egg and soup");
 
                initDish(s, dishes, "meatball soup", 6, "dry noodles with soup");

                s.setDishes(dishes);

                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                foodClique.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(foodClique);
            }

            if (storeSessionBeanLocal.readStoreByEmail("foodcliquefj@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Fruit and Juice");
                Customer c = new Customer();
                c.setEmail("foodcliquefj@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(foodClique);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes,"apple juice", 0,"");
                
                 //create a dummy dish in the store 
                initDish(s, dishes, "pear juice", 1, "");
 
                initDish(s, dishes, "carrot juice", 1, "");
 
                initDish(s, dishes, "cucumber juice", 1, "");
 
                initDish(s, dishes, "banana milk shake", 1, "");

                s.setDishes(dishes);
                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                foodClique.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(foodClique);
            }

            if (storeSessionBeanLocal.readStoreByEmail("foodcliquea@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Astons");
                Customer c = new Customer();
                c.setEmail("foodcliquea@gmail.com");
                c.setPassword("password");
                c.setUserType(UserType.VENDOR);
                customerSessionBeanLocal.createCustomer(c);
                customerSessionBeanLocal.createCustomer(c);
                s.setVendor(c);
                s.setCanteen(foodClique);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                storeSessionBeanLocal.createStore(s);
                initDish(s, dishes, "fiery chicken", 20, "actually very spicy");
                initDish(s, dishes, "chargrill chicken", 20, "");
                initDish(s, dishes, "black pepper chicken", 20, "");
                initDish(s, dishes, "teriyaki chicken", 20, "");
                initDish(s, dishes, "new york strip", 20, "");
                s.setDishes(dishes);
                storeSessionBeanLocal.updateStore(s);
                stores.add(s);
                foodClique.setStores(stores);
                canteenSessionBeanLocal.updateCanteen(foodClique);
            }
            canteenSessionBeanLocal.updateCanteen(foodClique);
        }

    }

    private void initDish(Store s, List<Dish> dishes, String dishName, int i, String description) throws DishNotFoundException {
        Dish dish = new Dish(dishName, i * Math.random(), description);
        dish.setIsAvailable(true);
        dishSessionBeanLocal.createDish(dish);
        dishes.add(dish);
        dish.setStore(s);
        dishSessionBeanLocal.updateDish(dish);
    }

    //input: a user
    //description: this method creates a customer order for input user(store -> item -> orderitem -> custoemrorder)
    private void initialiseCustomerOrderForUser(Customer customer) {
        try {
//            Customer customer = customerSessionBeanLocal.readCustomer(c.getId());
            System.out.println("CALLED HERE");
            List<Store> storeList = storeSessionBeanLocal.readAllStore();
//            Store store = storeList.get((int) (Math.random() * storeList.size()));
            Store store = storeList.get(0);
            if (!store.getDishes().isEmpty()) {
                OrderDish orderDish = new OrderDish();
                Dish dish = store.getDishes().get(0);
                orderDish.setDish(dish);
                orderDish.setAmount(1 + (int) (Math.random() * 10));
                orderDishSessionBean.createOrderDish(orderDish);

                List<OrderDish> orderDishes = dish.getOrderDishes();
                orderDishes.add(orderDish);
                dish.setOrderDishes(orderDishes);
                dishSessionBeanLocal.updateDish(dish);

                CustomerOrder customerOrder = new CustomerOrder();
                customerOrder.setCustomer(customer);
                customerOrder.setOrderDishes(Arrays.asList(orderDish));
                customerOrderSessionBeanLocal.createCustomerOrder(customerOrder);
                orderDish.setCustomerOrder(customerOrder);
                orderDishSessionBean.updateOrderDish(orderDish);

                System.out.println("OK");
                CustomerOrderType InBasketType =
                        customerOrderTypeSessionBeanLocal.readCustomerOrderTypeByName("IN BASKET").get(0);
                System.out.println("OK1");
                customerOrder.setCustomerOrderType(InBasketType);
                InBasketType.getCustomerOrders().add(customerOrder);
                customerOrderTypeSessionBeanLocal.updateCustomerOrderType(InBasketType);
                customerOrderSessionBeanLocal.updateCustomerOrder(customerOrder);

                customer.getCustomerOrders().add(customerOrder);
                customerSessionBeanLocal.updateCustomer(customer);
            }
        } catch (Exception ex) {
            Logger.getLogger(InitializeSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
