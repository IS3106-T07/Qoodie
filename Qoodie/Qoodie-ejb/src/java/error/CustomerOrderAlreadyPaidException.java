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
public class CustomerOrderAlreadyPaidException extends Exception {

    /**
     * Creates a new instance of <code>CustomerOrderAlreadyPaidException</code>
     * without detail message.
     */
    public CustomerOrderAlreadyPaidException() {
    }

    /**
     * Constructs an instance of <code>CustomerOrderAlreadyPaidException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerOrderAlreadyPaidException(String msg) {
        super(msg);
    }
}
