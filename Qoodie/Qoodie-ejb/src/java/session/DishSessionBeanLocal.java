/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Dish;
import error.DishNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface DishSessionBeanLocal {
    
    public void createDish(Dish d);
    public Dish readDish(Long dId) throws DishNotFoundException;
    public void updateDish(Dish d) throws DishNotFoundException;
    public void deleteDish(Dish d) throws DishNotFoundException;
    
    public List<Dish> readAllDishes();
}
