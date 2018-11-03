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
public class CanteenNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CanteenNotFoundException</code> without
     * detail message.
     */
    public CanteenNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CanteenNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CanteenNotFoundException(String msg) {
        super(msg);
    }
}
