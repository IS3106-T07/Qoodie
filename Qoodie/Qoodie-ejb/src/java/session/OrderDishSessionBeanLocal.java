/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.OrderDish;
import error.OrderDishNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface OrderDishSessionBeanLocal {
    
   public void createOrderDish(OrderDish d);
    public OrderDish readOrderDish(Long cId) throws OrderDishNotFoundException;
    public void updateOrderDish(OrderDish d) throws OrderDishNotFoundException;
    public void deleteOrderDish(OrderDish d) throws OrderDishNotFoundException;
    
    public List<OrderDish> readAllOrderDish();//for init
    
}
