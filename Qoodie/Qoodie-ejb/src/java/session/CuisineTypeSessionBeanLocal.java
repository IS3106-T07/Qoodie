/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CuisineType;
import error.CuisineTypeNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface CuisineTypeSessionBeanLocal {
    public void createCuisineType(CuisineType c);
    public CuisineType readCuisineType(Long cId) throws CuisineTypeNotFoundException;
    public void updateCuisineType(CuisineType c)throws CuisineTypeNotFoundException;
    public void deleteCuisineType(CuisineType c)throws CuisineTypeNotFoundException;
    
    public List<CuisineType> readAllCuisineType(); //for init
}
