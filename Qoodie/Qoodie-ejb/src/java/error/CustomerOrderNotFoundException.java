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
public class CustomerOrderNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CustomerOrderNotFoundException</code>
     * without detail message.
     */
    public CustomerOrderNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CustomerOrderNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerOrderNotFoundException(String msg) {
        super(msg);
    }
}
