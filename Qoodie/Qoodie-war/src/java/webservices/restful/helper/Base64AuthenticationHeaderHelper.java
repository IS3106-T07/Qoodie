/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful.helper;

import java.util.Base64;
import java.util.StringTokenizer;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author alex_zy
 */
public class Base64AuthenticationHeaderHelper {

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
    
    
    public static String getUsernameOrErrorResponseString(String authenticationHeader) {
    
        if (authenticationHeader == null || authenticationHeader.length() == 0) { // CASE: no auth token in the header
            return "authentication informaiton not found";
        } else {
            String authToken = authenticationHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String decodedString = new String(Base64.getDecoder().decode(authToken));
            StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
            String username = tokenizer.nextToken();
            return username;
        }
    }
    
    public static String getPasswordOrErrorResponseString(String authenticationHeader) {
    
        if (authenticationHeader == null || authenticationHeader.length() == 0) { // CASE: no auth token in the header
            return "authentication informaiton not found";
        } else {
            String authToken = authenticationHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String decodedString = new String(Base64.getDecoder().decode(authToken));
            StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
            String password = tokenizer.nextToken();
            return password;
        }
    }
}
