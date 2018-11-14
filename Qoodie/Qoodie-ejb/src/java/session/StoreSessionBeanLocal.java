/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Store;
import error.StoreNotFoundException;

import javax.ejb.Local;
import java.util.List;

/**
 *
 * @author alex_zy
 */
@Local
public interface StoreSessionBeanLocal {
    
    public void createStore(Store s); 
    public Store readStore(Long sId) throws StoreNotFoundException;
    public void updateStore(Store s) throws StoreNotFoundException;
    public void deleteStore(Store s) throws StoreNotFoundException;
    public void updateVendor(Store s) throws StoreNotFoundException;
    public Boolean checkVendorUserName(String username);
    public List<Store> readAllStore();
    Store retrieveStoreById(Long id);

    public List<Store> readStoreByEmail(String email);
}
