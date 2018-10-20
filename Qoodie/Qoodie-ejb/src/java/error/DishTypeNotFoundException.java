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
public class DishTypeNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>DishTypeNotFoundException</code> without
     * detail message.
     */
    public DishTypeNotFoundException() {
    }

    /**
     * Constructs an instance of <code>DishTypeNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DishTypeNotFoundException(String msg) {
        super(msg);
    }
}
