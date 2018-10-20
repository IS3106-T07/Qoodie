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
public class DishNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>DishNotFoundException</code> without
     * detail message.
     */
    public DishNotFoundException() {
    }

    /**
     * Constructs an instance of <code>DishNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DishNotFoundException(String msg) {
        super(msg);
    }
}
