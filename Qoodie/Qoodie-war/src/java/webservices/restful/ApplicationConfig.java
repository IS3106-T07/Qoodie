/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package webservices.restful;


import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author alex_zy
 */
@javax.ws.rs.ApplicationPath("Resource")
public class ApplicationConfig extends Application {
    
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.provider.classnames",
                "org.glassfish.jersey.media.multipart.MultiPartFeature");
        return props;
    }
    
    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(webservices.restful.CORSFilter.class);
        resources.add(webservices.restful.CanteensResources.class);
        resources.add(webservices.restful.CustomerOrderResource.class);
        resources.add(webservices.restful.CustomerOrderTypesResource.class);
        resources.add(webservices.restful.CustomersResource.class);
        resources.add(webservices.restful.OrderDishesResource.class);
        resources.add(webservices.restful.StoresResources.class);
        resources.add(webservices.restful.VendorResource.class);
    }
    
    
//    @Override
//    public Set<Object> getSingletons() {
//        Set objs = new HashSet();
//        objs.add(new CustomerFilter());
//        return objs;
//    }
}
