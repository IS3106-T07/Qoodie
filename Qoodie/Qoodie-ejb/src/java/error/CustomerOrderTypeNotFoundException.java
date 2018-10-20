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
public class CustomerOrderTypeNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CustomerOrderTypeNotFoundException</code>
     * without detail message.
     */
    public CustomerOrderTypeNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CustomerOrderTypeNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerOrderTypeNotFoundException(String msg) {
        super(msg);
    }
}
