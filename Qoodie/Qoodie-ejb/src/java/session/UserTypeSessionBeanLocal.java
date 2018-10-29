/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.UserType;
import error.UserTypeNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author alex_zy
 */
@Local
public interface UserTypeSessionBeanLocal {
    public void createUserType(UserType u);
    public UserType readUserType(Long uId) throws UserTypeNotFoundException;
    public void updateUserType(UserType u) throws UserTypeNotFoundException; 
    public void  deleteUserType(UserType u) throws UserTypeNotFoundException;
    
    public List<UserType> readAllUserType();//for init
}
