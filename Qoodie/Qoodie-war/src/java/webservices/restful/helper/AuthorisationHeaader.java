/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful.helper;

import java.util.Base64;

/**
 *
 * @author alex_zy
 */
public class AuthorisationHeaader {

    public static String vendor = "vendor1@gmail.com:password";
    public static String customer = "alice@gmail.com:password";
    public static String customer2 = "bob@gmail.com:password";
    
    public static void main(String[] args) {
        
        byte[] encodedBytes = Base64.getEncoder().encode(vendor.getBytes());
        System.out.println("encoded vendor header 1 " + new String(encodedBytes));
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
        System.out.println("decoded vendor header 1 " + new String(decodedBytes));
        
        encodedBytes = Base64.getEncoder().encode(customer.getBytes());
        System.out.println("encoded customer header 1 " + new String(encodedBytes));
        decodedBytes = Base64.getDecoder().decode(encodedBytes);
        System.out.println("decoded customer header 1 " + new String(decodedBytes));
        
        encodedBytes = Base64.getEncoder().encode(customer2.getBytes());
        System.out.println("encoded customer header 1 " + new String(encodedBytes));
        decodedBytes = Base64.getDecoder().decode(encodedBytes);
        System.out.println("decoded customer header 1 " + new String(decodedBytes));
        
    }
}
