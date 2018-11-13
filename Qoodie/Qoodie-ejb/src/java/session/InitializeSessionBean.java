/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Canteen;
import entity.CuisineType;
import entity.Customer;
import entity.CustomerOrder;
import entity.CustomerOrderType;
import entity.Dish;
import entity.DishType;
import entity.OrderDish;
import entity.Store;
import entity.UserType;
import error.CustomerNotFoundException;
import error.CustomerOrderNotFoundException;
import error.CanteenNotFoundException;
import error.CuisineTypeNotFoundException;
import error.CustomerOrderTypeNotFoundException;
import error.DishNotFoundException;
import error.OrderDishNotFoundException;
import error.StoreNotFoundException;
import java.util.ArrayList;
import java.util.Date;
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

            List<UserType> userTypes = userTypeSessionBeanLocal.readAllUserType();
            if (userTypes.size() != userTypeArr.length) {
                initializeUserType();
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
            initializeOrderDish();
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

    public void initializeUserType() {
        for (int i = 0; i < userTypeArr.length; i++) {
            UserType u = new UserType();
            u.setName(userTypeArr[i]);
            u.setCustomers(new ArrayList<>());
            userTypeSessionBeanLocal.createUserType(u);
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
            c.setUserType(userTypeSessionBeanLocal.readAllUserType().get(0));
            customerSessionBeanLocal.createCustomer(c);
            initialiseCustomerOrderForUser(c);
        }
        if (customerSessionBeanLocal.readCustomerByEmail("bob@gmail.com").isEmpty()) {
            Customer c = new Customer();
            c.setName("Bob");
            c.setEmail("bob@gmail.com");
            c.setUserType(userTypeSessionBeanLocal.readAllUserType().get(1));
            c.setPhone("66666666");
            c.setAddress("31 Sungei Kadut Loop S 729509, Singapore");
            c.setPassword("password");
            customerSessionBeanLocal.createCustomer(c);
            initialiseCustomerOrderForUser(c);
        }

    }

    private void initializeCanteen() {
        List<Canteen> canteens = new ArrayList<Canteen>();

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
                s.setPassword("password");
                s.setVendorEmail("vendor1@gmail.com");
                s.setCanteen(fineFood);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Bak Choy", 2 * Math.random(), "high fiber and full of vitamin");
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
                s.setPassword("password");
                s.setVendorEmail("finefoodgc@gmail.com");
                s.setCanteen(fineFood);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Earl Grey Milk Team", 4 * Math.random(), "you can choose your own sugar level");
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
                s.setPassword("password");
                s.setVendorEmail("finefoodk@gmail.com");
                s.setCanteen(fineFood);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Chicken Kimchi Soup", 7 * Math.random(), "it also has bak choya and vermecilli");
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
                s.setPassword("password");
                s.setVendorEmail("deckytf@gmail.com");
                s.setCanteen(deck);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Fish Ball", 2 * Math.random(), "Made of real fish!");
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
                s.setPassword("password");
                s.setVendorEmail("deckcp@gmail.com");
                s.setCanteen(deck);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("braised chicken with potato", 7 * Math.random(), "traditional chinese style");
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
                s.setPassword("password");
                s.setVendorEmail("deckcr@gmail.com");
                s.setCanteen(deck);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Roasted Chicken Rice", 5 * Math.random(), "popular item");
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
                s.setPassword("password");
                s.setVendorEmail("foodclickbm@gmail.com");
                s.setCanteen(foodClick);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("Fish and Mushroom Bam Mian", 6 * Math.random(), "you can choose in soup or dried");
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
                s.setPassword("password");
                s.setVendorEmail("foodclickfj@gmail.com");
                s.setCanteen(foodClick);
                storeSessionBeanLocal.createStore(s);
                foodClick.getStores().add(s);
                storeSessionBeanLocal.updateStore(s);
            }

            if (storeSessionBeanLocal.readStoreByEmail("foodclicka@gmail.com").isEmpty()) {
                Store s = new Store();
                s.setName("Astons");
                s.setPassword("password");
                s.setVendorEmail("foodclicka@gmail.com");
                s.setCanteen(foodClick);
                //create a dummy dish in the store 
                List<Dish> dishes = new ArrayList<>();
                Dish dish = new Dish("fiery chicken", 20 * Math.random(), "actually very spicy");
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
                orderDish.setAmount(1+(int) (Math.random() * 10));
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
