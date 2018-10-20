/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.CuisineType;
import entity.Store;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author alex_zy
 */
@Startup
@Singleton
@LocalBean
public class InitializeSessionBean {
    @EJB
            CuisineTypeSessionBeanLocal cuisineTypeSessionBeanLocal;
    
    @PostConstruct
    public void init(){
        initializeCuisineType();
    }
    
    public void initializeCuisineType(){
        List<CuisineType> list = cuisineTypeSessionBeanLocal.readAllCuisineType();
        if (list.isEmpty()){
            String[] names = {"KOREAN", "JAPANESE", "CHINESE", "WESTERN", "DRINK", "FRUIT", "INDIAN", "MALAY"};
            for (int i = 0; i < names.length; i++){
                CuisineType c = new CuisineType();
                c.setName(names[i]);
                c.setStores(new ArrayList<>());
                cuisineTypeSessionBeanLocal.createCuisineType(c);
            }
        }
    }
}
