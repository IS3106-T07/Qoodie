/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful.helper;

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
import java.util.List;

/**
 *
 * @author alex_zy
 */
public class Flattener {

    public static Customer flatten(Customer c) {
        c.getUserType().setCustomers(null);
        List<CustomerOrder> orders = c.getCustomerOrders();
        for (CustomerOrder co : orders) {
            co.setCustomer(null);
            for (CustomerOrder order : orders) {

                for (OrderDish od : order.getOrderDishes()) {
                    od.setCustomerOrder(null);
                    od.getDish().setOrderDishes(null);
                    od.getDish().setDishType(null);
                    od.getDish().getStore().setDishes(null);
                    od.getDish().getStore().setCuisineType(null);
                    od.getDish().getStore().setVendorEmail(null);
                    od.getDish().getStore().setPassword(null);
                    od.getDish().getStore().getCanteen().setStores(null);

                }

            }
            co.getCustomerOrderType().setCustomerOrders(null);
        }
        c.getUserType().setCustomers(null);
        return c;
    }

    public static CustomerOrder flatten(CustomerOrder co) {
        co.getCustomer().setCustomerOrders(null);
        co.getCustomerOrderType().setCustomerOrders(null);
        List<OrderDish> orderDishes = co.getOrderDishes();
        for (OrderDish od : orderDishes) {
            od.setCustomerOrder(null);
            od.getDish().setOrderDishes(null);
            od.getDish().setDishType(null);
            od.getDish().getStore().setDishes(null);
            od.getDish().getStore().setCuisineType(null);
            od.getDish().getStore().setVendorEmail(null);
            od.getDish().getStore().setPassword(null);
            od.getDish().getStore().getCanteen().setStores(null);
        }
        return co;
    }

    public static Store flatten(Store s) {
        if (s.getCanteen() != null) {
            s.getCanteen().setStores(null);
        }
        if (s.getCuisineType() != null) {
            s.getCuisineType().setStores(null);
        }
        for (Dish d : s.getDishes()) {
            if (d.getDishType() != null) {
                d.getDishType().setDishes(null);
            }
            d.setStore(null);
            d.setOrderDishes(null);
        }

        return s;
    }

    public static Canteen flatten(Canteen c) {
        for (Store s : c.getStores()) {
            s.setCanteen(null);
            for (Dish d : s.getDishes()) {
                d.setStore(null);
            }
        }
        return c;
    }

    public static CuisineType flatten(CuisineType ct) {
        for (Store s : ct.getStores()) {
            s.setCanteen(null);
        }
        return ct;
    }

    public static CustomerOrderType flatten(CustomerOrderType ot) {
        for (CustomerOrder co : ot.getCustomerOrders()) {
            co.setCustomerOrderType(null);
        }
        return ot;
    }

    public static Dish flatten(Dish d) {
        if (d.getDishType() != null) {
            d.getDishType().setDishes(null);
        }
        for (OrderDish od : d.getOrderDishes()) {
            od.setDish(null);
        }
        d.getStore().setDishes(null);
        return d;
    }

    public static DishType flatten(DishType dt) {
        for (Dish d : dt.getDishes()) {
            d = flatten(d);
            d.setDishType(null);
        }
        return dt;
    }

    public static OrderDish flatten(OrderDish od) {
        od.setCustomerOrder(null);
        od.setDish(flatten(od.getDish()));
        return od;
    }

    public static UserType flatten(UserType ut) {
        for (Customer c : ut.getCustomers()) {
            c.setUserType(null);
            for (CustomerOrder co : c.getCustomerOrders()) {
                co.setOrderDishes(null);
            }
        }
        return ut;
    }

}
