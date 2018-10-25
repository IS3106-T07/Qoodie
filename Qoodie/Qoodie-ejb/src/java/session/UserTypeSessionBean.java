/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package session;

import entity.UserType;
import error.UserTypeNotFoundException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alex_zy
 */
@Stateless
public class UserTypeSessionBean implements UserTypeSessionBeanLocal {
    @PersistenceContext(unitName = "Qoodie-ejbPU")
    private EntityManager em;
    
    @Override
    public void createUserType(UserType u) {
        em.persist(u);
    }
    
    @Override
    public UserType readUserType(Long uId) throws UserTypeNotFoundException {
        UserType u = em.find(UserType.class, uId);
        if (u == null) throw new UserTypeNotFoundException("user type not found");
        return u;
    }
    
    @Override
    public void updateUserType(UserType u) throws UserTypeNotFoundException {
        UserType oldU = readUserType(u.getId());
        oldU.setName(u.getName());
    }
    
    @Override
    public void deleteUserType(UserType u) throws UserTypeNotFoundException {
        em.remove(readUserType(u.getId()));
    }
    
    @Override
    public List<UserType> readAllUserType() {
        return em.createQuery("SELECT u from UserType u").getResultList();
    }
    
}
