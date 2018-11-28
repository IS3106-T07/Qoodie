/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful.helper;

import entity.*;

import java.util.List;

/**
 *
 * @author alex_zy
 */
public class Flattener {

    public static Customer flatten(Customer c) {
        List<CustomerOrder> orders = c.getCustomerOrders();
        for (CustomerOrder co : orders) {
            co.setCustomer(null);
            for (CustomerOrder order : orders) {

                for (OrderDish od : order.getOrderDishes()) {
                    cleanupOrderDish(od);

                }

            }
            co.getCustomerOrderType().setCustomerOrders(null);
        }
        return c;
    }

    private static void cleanupOrderDish(OrderDish od) {
        od.setCustomerOrder(null);
        od.getDish().setOrderDishes(null);
        od.getDish().setDishType(null);
        od.getDish().getStore().setDishes(null);
        od.getDish().getStore().setCuisineType(null);
        od.getDish().getStore().setVendor(null);
        od.getDish().getStore().getCanteen().setStores(null);
    }

    public static CustomerOrder flatten(CustomerOrder co) {
        co.getCustomer().setCustomerOrders(null);
        co.getCustomerOrderType().setCustomerOrders(null);
        List<OrderDish> orderDishes = co.getOrderDishes();
        System.out.println("Order Dish Size: " + orderDishes.size());
        for (OrderDish od : orderDishes) {
            cleanupOrderDish(od);
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
                d.setOrderDishes(null);
                if (d.getDishType() != null) {
                    d.getDishType().setDishes(null);
                }
            }
            if (s.getCuisineType() != null) {
                s.getCuisineType().setStores(null);
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
}
