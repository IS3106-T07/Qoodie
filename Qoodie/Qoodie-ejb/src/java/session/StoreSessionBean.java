/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Store;
import error.StoreNotFoundException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

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
        Store s = em.find(Store.class, sId);
        if (s == null) {
            throw new StoreNotFoundException("store not found");
        }
        return s;
    }

    @Override  //partial stores with null field are possible as a result of PUT
    public void updateStore(Store newS) throws StoreNotFoundException {
        Store oldS = readStore(newS.getId());

        if (newS.getDishes() != null) {
            oldS.setDishes(newS.getDishes());
        }
        if (newS.getName() != null) {
            oldS.setName(newS.getName());
        }
        if (newS.getVendor().getPassword() != null) {
             oldS.getVendor().setPassword(newS.getVendor().getPassword());
        }
        if (newS.getVendor().getEmail() != null) {
            oldS.getVendor().setEmail(newS.getVendor().getEmail());
        }
        if (newS.getCuisineType()!=null){
            oldS.setCuisineType(newS.getCuisineType());
        }
    }

    @Override
    public void deleteStore(Store s) throws StoreNotFoundException {
        Store oldS = readStore(s.getId());
        em.remove(oldS);
    }

    @Override
    public void updateVendor(Store s) throws StoreNotFoundException {
        em.merge(s);
    }

    @Override
    public Boolean checkVendorEmail(String email) {
        Query query = em.createQuery("SELECT s FROM Store s WHERE s.vendor.email = :email");
        query.setParameter("email", email);
        return query.getResultList().size() == 0;
    }

    @Override
    public List<Store> readAllStore() {
        return em.createQuery("SELECT s From Store s").getResultList();
    }

    @Override
    public Store retrieveStoreById(Long id) {
        return em.find(Store.class, id);
    }

    @Override
    public List<Store> readStoreByEmail(String email) {
        List<Store> returned = new ArrayList<>();
        List<Store> all = readAllStore();
        for (Store c : all) {
            if (c.getVendor().getEmail().toLowerCase().equals(email.toLowerCase())) {
                returned.add(c);
            }
        }
        return returned;
    }

}
