/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package filter;

import entity.Customer;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.CustomerSessionBeanLocal;

/**
 *
 * @author alex_zy
 */
public class AdminFilter implements Filter {
    CustomerSessionBeanLocal customerSessionBeanLocal = lookupCustomerSessionBeanLocal();
    private static final String AUTHORIZATION_HEADER = "Authorization"; //the key we will be looking for in the header
    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
    private static final String SECURED_URL_PREFIX = "customers";
    
    
    
    public AdminFilter() {
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("*****initializing servlet filter*****");
    }
    
    
    @Override
    public void destroy() {
        
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || authHeader.length() == 0){ // CASE: no auth token in the header
            throwNotLoggedIn(response);
        }
        else
        {
            String authToken = authHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
            String decodedString = new String(Base64.getDecoder().decode(authToken));
            StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
            String email = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            List<Customer> customerList = customerSessionBeanLocal.readCustomerByEmail(email);
            if (customerList.isEmpty()) //CASE: wrong email or password
            {
                throwUnauthorized(response);
            }
            else
            {
                Customer customer = customerList.get(0);
                if (!customer.getUserType().getName().toLowerCase().equals("admin")) //CASE: not admin user
                {
                    throwForbidden(response);
                }
                else
                {
                    chain.doFilter(request, response);
                    
                }
            }
        }
        
        
        
    }
    
    private void throwNotLoggedIn(ServletResponse res) throws IOException{
        HttpServletResponse response = (HttpServletResponse) res;
        
        response.reset();
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "you need to login first")
                .build();
        response.getWriter().write(exception.toString());
    }
    
    private void throwUnauthorized(ServletResponse res) throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;
        
        response.reset();
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "user does not exist")
                .build();
        response.getWriter().write(exception.toString());
    }
    
    private void throwForbidden(ServletResponse res) throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;
        
        response.reset();
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        JsonObject exception = Json.createObjectBuilder()
                .add("message", "you are not authorised for this action")
                .build();
        response.getWriter().write(exception.toString());
        
    }

    private CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/CustomerSessionBean!session.CustomerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
