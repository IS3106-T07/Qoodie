/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Store;
import error.StoreNotFoundException;
import java.util.List;
import javax.ejb.Local;

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
    
    public List<Store> readAllStore();
}
