/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CuisineType;
import error.CuisineTypeNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class CuisineTypeSessionBean implements CuisineTypeSessionBeanLocal {
    
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;

    @Override
    public void createCuisineType(CuisineType c) {
        em.persist(c);
    }

    @Override
    public CuisineType readCuisineType(Long cId) throws CuisineTypeNotFoundException {
        CuisineType c = em.find(CuisineType.class, cId);
        if ( c == null ) throw new CuisineTypeNotFoundException("cuisine type not found");
        return c;
    }

    @Override
    public List<CuisineType> readAllCuisineType() {
        return (em.createQuery("SELECT f FROM CuisineType f").getResultList());
    }

}
