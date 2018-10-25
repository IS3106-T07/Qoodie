/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Store;
import error.StoreNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class StoreSessionBean implements StoreSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;

    @Override
    public void createStore(Store s) {
        em.persist(s);
    }

    @Override
    public Store readStore(Long sId) throws StoreNotFoundException {
        Store s =em.find(Store.class, sId);
        if (s == null) throw new StoreNotFoundException("store not found");
        return s;
    }

    @Override
    public void updateStore(Store newS) throws StoreNotFoundException {
        Store oldS = readStore(newS.getId());
        oldS.setDishes(newS.getDishes());
        oldS.setName(newS.getName());
        oldS.setPassword(newS.getPassword());
        oldS.setVendorEmail(newS.getVendorEmail());
    }

    @Override
    public void deleteStore(Store s) throws StoreNotFoundException {
        Store oldS = readStore(s.getId());
        em.remove(oldS);
    }

    @Override
    public List<Store> readAllStore() {
        return em.createQuery("SELECT s From Store s").getResultList();
    }
    
}
