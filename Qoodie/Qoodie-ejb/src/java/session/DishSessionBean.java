/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.Dish;
import error.DishNotFoundException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
        System.out.println("CREATED DISH " + d.getName());
    }
    
    @Override
    public Dish readDish(Long dId) throws DishNotFoundException {
        Dish c = em.find(Dish.class, dId);
        if (c==null) throw new DishNotFoundException("custoemr order type not found");
        return c;
    }
    
    @Override
    public void updateDish(Dish d) throws DishNotFoundException {
        em.merge(d);
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
