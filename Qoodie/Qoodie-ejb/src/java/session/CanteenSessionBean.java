/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Canteen;
import error.CanteenNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class CanteenSessionBean implements CanteenSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;

    @Override
    public void createCanteen(Canteen c) {
        em.persist(c);
    }

    @Override
    public Canteen readCanteen(Long cId) throws CanteenNotFoundException {
        Canteen c = em.find(Canteen.class, cId);
        if(c == null){ 
            throw new CanteenNotFoundException("canteen not found");
        }
        return c;
    }
    

    @Override
    public void updateCanteen(Canteen newC) throws CanteenNotFoundException {
        Canteen oldC = readCanteen(newC.getId());
        oldC.setName(newC.getName());
        oldC.setStores(newC.getStores());
    }

    @Override
    public void deleteCanteen(Long cId) throws CanteenNotFoundException {
        Canteen c = readCanteen(cId);
        em.remove(c);
    }

    @Override
    public List<Canteen> readAllCanteen() {
        return em.createQuery("SELECT c FROM Canteen c").getResultList();
    }

    @Override
    public List<Canteen> readCanteenByName(String name) {
        return em.createQuery("SELECT c FROM Canteen c WHERE c.name = \"" + name+"\"").getResultList();   
    }

}
