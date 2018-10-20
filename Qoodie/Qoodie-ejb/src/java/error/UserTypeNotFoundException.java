/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package error;

/**
 *
 * @author alex_zy
 */
public class UserTypeNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>UserTypeNotFoundException</code> without
     * detail message.
     */
    public UserTypeNotFoundException() {
    }

    /**
     * Constructs an instance of <code>UserTypeNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UserTypeNotFoundException(String msg) {
        super(msg);
    }
}
