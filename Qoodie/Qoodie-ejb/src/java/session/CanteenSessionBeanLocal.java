/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Canteen;
import error.CanteenNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface CanteenSessionBeanLocal {
    
    public void createCanteen(Canteen c);
    public Canteen readCanteen(Long cId) throws CanteenNotFoundException;
    public void updateCanteen(Canteen newC) throws CanteenNotFoundException;
    public void deleteCanteen(Long cId) throws CanteenNotFoundException;
    
    public List<Canteen> readAllCanteen();
    
}
