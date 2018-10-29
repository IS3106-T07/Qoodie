/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.DishType;
import error.DishTypeNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface DishTypeSessionBeanLocal {
    
    public void createDishType(DishType d);
    public DishType readDishType(Long cId) throws DishTypeNotFoundException;
    public void updateDishType(DishType d) throws DishTypeNotFoundException;
    public void deleteDishType(DishType d) throws DishTypeNotFoundException;
    
    public List<DishType> readAllDishType();//for init
}
