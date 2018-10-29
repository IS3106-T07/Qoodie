/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.Dish;
import error.DishNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class DishSessionBean implements DishSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;
    
    @Override
    public void createDish(Dish d) {
        em.persist(d);
    }
    
    @Override
    public Dish readDish(Long dId) throws DishNotFoundException {
        Dish c = em.find(Dish.class, dId);
        if (c==null) throw new DishNotFoundException("custoemr order type not found");
        return c;
    }
    
    @Override
    public void updateDish(Dish d) throws DishNotFoundException {
        Dish oldD = readDish(d.getId());
        oldD.setDescription(d.getDescription());
        oldD.setDishType(d.getDishType());
        oldD.setIsAvailable(d.getIsAvailable());
        oldD.setName(d.getName());
        oldD.setOrderDishes(d.getOrderDishes());
        oldD.setPrice(d.getPrice());
        oldD.setStore(d.getStore());
    }
    
    @Override
    public void deleteDish(Dish d) throws DishNotFoundException {
        em.remove(readDish(d.getId()));
    }
    
    @Override
    public List<Dish> readAllDishes() {
        return em.createQuery("SELECT c From Dish c").getResultList();
    }
}
