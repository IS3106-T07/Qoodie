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
public class CuisineTypeNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>CuisineTypeNotFoundException</code>
     * without detail message.
     */
    public CuisineTypeNotFoundException() {
    }

    /**
     * Constructs an instance of <code>CuisineTypeNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CuisineTypeNotFoundException(String msg) {
        super(msg);
    }
}
