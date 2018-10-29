/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.DishType;
import error.DishTypeNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class DishTypeSessionBean implements DishTypeSessionBeanLocal {
@PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;
    @Override
    
    public void createDishType(DishType d) {
        em.persist(d);
    }

    @Override
    public DishType readDishType(Long cId) throws DishTypeNotFoundException {
        DishType c = em.find(DishType.class, cId);
        if ( c == null ) throw new DishTypeNotFoundException("dish type not found");
        return c;
    }

    @Override
    public void updateDishType(DishType d) throws DishTypeNotFoundException {
        DishType oldD = readDishType(d.getId());
        oldD.setName(d.getName());
        oldD.setDishes(d.getDishes());
    }

    @Override
    public void deleteDishType(DishType d) throws DishTypeNotFoundException {
       em.remove(readDishType(d.getId()));
    }
    
    
    @Override
    public List<DishType> readAllDishType() {
        return em.createQuery("SELECT f FROM DishType f").getResultList();
    }

    
}
