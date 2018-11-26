/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.*;
import enums.UserType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import session.*;
import webservices.restful.datamodels.*;
import webservices.restful.helper.Flattener;
import webservices.restful.util.StorageDir;

import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static webservices.restful.util.ResponseHelper.getExceptionDump;

/**
 *
 * @author sinhv
 */
@Path("vendors")
public class VendorResource {
    CustomerOrderTypeSessionBeanLocal customerOrderTypeSessionLocal = lookupCustomerOrderTypeSessionBeanLocal();
    DishTypeSessionBeanLocal dishTypeSessionBean = lookupDishTypeSessionBeanLocal();
    FileDirectorySessionLocal fileDirectorySessionLocal = lookupFileDirectorySessionLocal();
    CustomerSessionBeanLocal customerSessionBeanLocal = lookupCustomerSessionBeanLocal();
    CanteenSessionBeanLocal canteenSessionBeanLocal = lookupCanteenSessionBeanLocal();
    DishSessionBeanLocal dishSessionBeanLocal = lookupDishSessionBeanLocal();
    CustomerOrderSessionBeanLocal customerOrderSessionBeanLocal = lookupCustomerOrderSessionBeanLocal();
    OrderDishSessionBeanLocal orderDishSessionBean = lookupOrderDishSessionBeanLocal();
    StoreSessionBeanLocal storeSessionBeanLocal = lookupStoreSessionBeanLocal();
    private LinkedHashMap<Object, Object> error = new LinkedHashMap<>();
    private String storageDir = StorageDir.dir;
    private String tempDir = storageDir + "temp/";
    private String persistDir = storageDir + "persist/";
    UserTransaction userTransaction = lookupUserTransaction();

    @javax.ws.rs.core.Context
    private HttpHeaders header;

    @javax.ws.rs.core.Context
    private HttpServletResponse response;

    @POST
    @Path("uploadImage")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("filepond") InputStream is,
                               @FormDataParam("filepond") FormDataContentDisposition fileDetails) {
        try {
            String fileName = fileDetails.getFileName();
            boolean mkdirSuccess = true;
            String path;
            int i = 0;
            long tmpId = 0L;
            do {
                if (!mkdirSuccess) {
                    Thread.sleep(1000);
                }
                tmpId = new Date().getTime();
                path = tempDir + tmpId;

                File file = new File(path);
                mkdirSuccess = file.mkdirs();
                i++;
            } while (!mkdirSuccess && i < 10);
            if (!mkdirSuccess) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            writeToFile(is, path + "/" + fileName);
            return Response.status(Response.Status.OK).entity(tmpId).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @GET
    @Path("getAllDishTypes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllDishTypes() {
        try {
            List<DishType> dishTypes = dishTypeSessionBean.readAllDishType();
            return  Response.status(Response.Status.OK).entity(dishTypes.stream().map(DishTypeRsp::new)
                    .collect(Collectors.toList())).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @GET
    @Path("getVendorDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getVendorDetails(@QueryParam("vendorId") Long id) {
        try {
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(id);
            return  Response.status(Response.Status.OK).entity(new StoreRsp(store)).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @GET
    @Path("setOrderToReady")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOrderToReady(@QueryParam("storeId") Long storeId,
                                    @QueryParam("orderId") Long orderId) {
        try {
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(storeId);
            OrderDish orderDish = orderDishSessionBean.readOrderDish(orderId);
            Dish dish = orderDish.getDish();
            Long sId = dish.getStore().getId();
            if (!sId.equals(storeId)) {
                error.put("message", "Unauthorised vendor");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            List<CustomerOrderType> ready = customerOrderTypeSessionLocal.readCustomerOrderTypeByName("READY");
            CustomerOrder customerOrder = orderDish.getCustomerOrder();
            customerOrder.setCustomerOrderType(ready.get(0));
            customerOrderSessionBeanLocal.updateCustomerOrder(customerOrder);
            return  Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @GET
    @Path("setOrderToDelivered")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOrderToDelivered(@QueryParam("storeId") Long storeId,
                                    @QueryParam("orderId") Long orderId) {
        try {
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(storeId);
            OrderDish orderDish = orderDishSessionBean.readOrderDish(orderId);
            Dish dish = orderDish.getDish();
            Long sId = dish.getStore().getId();
            if (!sId.equals(storeId)) {
                error.put("message", "Unauthorised vendor");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            List<CustomerOrderType> ready = customerOrderTypeSessionLocal.readCustomerOrderTypeByName("DELIVERED");
            CustomerOrder customerOrder = orderDish.getCustomerOrder();
            customerOrder.setCustomerOrderType(ready.get(0));
            customerOrderSessionBeanLocal.updateCustomerOrder(customerOrder);
            return  Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @GET
    @Path("getVendorOrders")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getVendorOrders(@QueryParam("vendorId") Long id,
                                    @QueryParam("startDate") Long startDate,
                                    @QueryParam("endDate") Long endDate) {
        try {
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(id);
            List<OrderRsp> orderRsps = new ArrayList<>();
            for (Dish dish : store.getDishes()) {
                for (OrderDish orderDish : dish.getOrderDishes()) {
                    CustomerOrder customerOrder = orderDish.getCustomerOrder();
                    CustomerOrderType orderStatus = customerOrder.getCustomerOrderType();
                    boolean checkDelivered = orderStatus.getName().toLowerCase().contains("delivered");
                    long time = customerOrder.getCreated().getTime();
                    boolean checkTime = time > startDate && time <= endDate;
                    if (checkDelivered && checkTime) orderRsps.add(new OrderRsp(orderDish));
                    else {
                        System.out.println(orderDish);
                    }
                }
            }
            return  Response.status(Response.Status.OK).entity(orderRsps).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @POST
    @Path("createNewItem")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewItem(CreateItemReq createItemReq) {
        try {
            Long fileId = createItemReq.getFileId();
            if (createItemReq.getFile() == null || fileId == null) {
                error.put("message", "No Image Provided");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            userTransaction.begin();
            Long vendorId = createItemReq.getVendorId();
            Customer customer = customerOrderSessionBeanLocal.getCustomerById(vendorId);
            if (customer == null) {
                error.put("message", "Vendor doesn't exist");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(vendorId);
            Long dishTypeId = createItemReq.getDishTypeId();
            DishType dishType = dishTypeSessionBean.readDishType(dishTypeId);
            if (dishType == null) {
                error.put("message", "Vendor doesn't exist");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            FileDirectoryEntity file = persistFile(createItemReq.getFile(), fileId, "/" + store.getName());
            Dish newItem = new Dish();
            newItem.setName(createItemReq.getName());
            newItem.setDescription(createItemReq.getDescription());
            newItem.setIsAvailable(true);
            newItem.setPrice(createItemReq.getPrice());
            newItem.setStore(store);
            newItem.setFileDirectoryEntity(file);
            newItem.setDishType(dishType);
            dishSessionBeanLocal.createDish(newItem);
            List<Dish> dishes = store.getDishes();
            dishes = dishes == null ? new ArrayList<>() : dishes;
            dishes.add(newItem);
            storeSessionBeanLocal.updateStore(store);
            userTransaction.commit();
            return  Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            try {
                userTransaction.setRollbackOnly();
                System.out.println("ROLLED BACK");
            } catch (SystemException e) {
                System.out.println("ROLLBACK FAILED");
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @POST
    @Path("updateItem")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateItem(CreateItemReq createItemReq) {
        try {
            userTransaction.begin();
            Long vendorId = createItemReq.getVendorId();
            Customer customer = customerOrderSessionBeanLocal.getCustomerById(vendorId);
            if (customer == null) {
                error.put("message", "Vendor doesn't exist");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(vendorId);

            Dish dish = dishSessionBeanLocal.readDish(createItemReq.getId());
            Customer vendor = dish.getStore().getVendor();
            if (!vendor.getId().equals(vendorId)) {
                error.put("message", "Unauthorised Vendor");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            dish.setName(createItemReq.getName());
            dish.setDescription(createItemReq.getDescription());
            dish.setIsAvailable(true);
            dish.setPrice(createItemReq.getPrice());

            Long fileId = createItemReq.getFileId();
            if (fileId != null) {
                FileDirectoryEntity file = persistFile(createItemReq.getFile(), fileId, "/" + store.getName());
                dish.setFileDirectoryEntity(file);
            }
            Long dishTypeId = createItemReq.getDishTypeId();
            if (dishTypeId != -1) {
                DishType dishType = dishTypeSessionBean.readDishType(dishTypeId);
                dish.setDishType(dishType);
            }
            dishSessionBeanLocal.updateDish(dish);
            userTransaction.commit();
            return  Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            try {
                userTransaction.setRollbackOnly();
                System.out.println("ROLLED BACK");
            } catch (SystemException e) {
                System.out.println("ROLLBACK FAILED");
            }
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @POST
    @Path("deleteItem")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteItem(CreateItemReq createItemReq) {
        try {
            userTransaction.begin();
            Long vendorId = createItemReq.getVendorId();
            Customer customer = customerOrderSessionBeanLocal.getCustomerById(vendorId);
            if (customer == null) {
                error.put("message", "Vendor doesn't exist");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }

            Dish dish = dishSessionBeanLocal.readDish(createItemReq.getId());
            Customer vendor = dish.getStore().getVendor();
            if (!vendor.getId().equals(vendorId)) {
                error.put("message", "Unauthorised Vendor");
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            dish.setIsAvailable(false);
            dishSessionBeanLocal.updateDish(dish);
            userTransaction.commit();
            return  Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            try {
                userTransaction.setRollbackOnly();
                System.out.println("ROLLED BACK");
            } catch (SystemException e) {
                System.out.println("ROLLBACK FAILED");
            }
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @POST
    @Path("revertImage")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response revertFile(String tmpId) {
        try {
            String path = tempDir + tmpId;

            File file = new File(path);
            boolean deleteResult = deleteFolder(file);
            if (!deleteResult) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("File Doesn't Exist").build();
            return  Response.status(Response.Status.OK).entity(tmpId).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @GET
    @Path("getOrdersByVendorId")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdersByVendorId(@QueryParam("vendorId") Long vendorId) {
        try {
            userTransaction.begin();
            Store store = storeSessionBeanLocal.retrieveStoreByVendorId(vendorId);
            if (store == null) {
                userTransaction.setRollbackOnly();
                error.put("message", "Unauthorise vendor");
                return  Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            List<OrderRsp> response = new ArrayList<>();
            List<Dish> dishes = store.getDishes();
            for (Dish dish : dishes) {
                for (OrderDish orderDish : dish.getOrderDishes()) {
                    response.add(new OrderRsp(orderDish));
                }
            }
            userTransaction.commit();
            return  Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception ex) {
            try {
                userTransaction.setRollbackOnly();
                System.out.println("ROLLED BACK");
            } catch (SystemException e) {
                System.out.println("ROLLBACK FAILED");
            }
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @GET
    @Path("getAllCanteens")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllCanteens() {
        try {
            List<Canteen> canteens = canteenSessionBeanLocal.readAllCanteen();
            List<LinkedHashMap<Object, Object>> results = new ArrayList<>();
            for (Canteen canteen : canteens) {
                LinkedHashMap<Object, Object> pair = new LinkedHashMap<>();
                pair.put("id", canteen.getId());
                pair.put("name", canteen.getName());
                results.add(pair);
            }
            return  Response.status(Response.Status.OK).entity(results).build();
        } catch (Exception ex) {
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @POST
    @Path("createVendor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVendor(CreateVendorReq createVendorReq) {
        try {
            userTransaction.begin();
            String emailAddress = createVendorReq.getEmailAddress().toLowerCase();
            if (emailAddress.contains(" ")) {
                userTransaction.setRollbackOnly();
                error.put("message", "Email contains spaces");
                return  Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            if (!storeSessionBeanLocal.checkVendorEmail(emailAddress)) {
                userTransaction.setRollbackOnly();
                error.put("message", "User Exists");
                return  Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            Store store = new Store();
            String password = createVendorReq.getPassword();
            store.setName(createVendorReq.getStoreName());
            Canteen canteen = canteenSessionBeanLocal.readCanteen(createVendorReq.getCanteen());
            List<Store> stores = canteen.getStores();
            stores = stores == null ? new ArrayList<>() : stores;
            stores.add(store);
            canteen.setStores(stores);
            storeSessionBeanLocal.createStore(store);
            canteenSessionBeanLocal.updateCanteen(canteen);

            Customer vendor = new Customer();
            vendor.setEmail(emailAddress);
            vendor.setPassword(password);
            vendor.setCreated(new Date());
            vendor.setIsActive(true);
            vendor.setUserType(UserType.VENDOR);
            customerSessionBeanLocal.createCustomer(vendor);
            store.setVendor(vendor);
            storeSessionBeanLocal.updateStore(store);

            userTransaction.commit();
            return  Response.status(Response.Status.OK).entity(Flattener.flatten(vendor)).build();
        } catch (Exception ex) {
            try {
                userTransaction.setRollbackOnly();
                System.out.println("ROLLED BACK");
            } catch (SystemException e) {
                System.out.println("ROLLBACK FAILED");
            }
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    @GET
    @Path("getFullImage")
    @Produces("image/png")
    public Response getFullImage(@QueryParam("id") String id) {
        File file = new File(persistDir + "dishes/meteora_small.jpg");

        return Response.ok(file).header("Content-Disposition",
                "attachment; filename=image_from_server.png").build();
    }

    @GET
    @Path("getImage")
    @Produces("image/png")
    public Response getImage(@QueryParam("id") String id) {
        try {
            String dir = persistDir + "dishes/meteora_small.jpg";
            BufferedImage image = ImageIO.read(new File(dir));

            if (image != null) {

//                image = ImageUtil.resize(image, width, height);

                response.setContentType("images/jpg");
//                response.setHeader("Content-Type", FileHelper.getNameFromPath(mmcpath));
//                response.setHeader("Content-Disposition", "inline; filename=\"" + FileHelper.getNameFromPath(mmcpath) + "\"");

                OutputStream out = response.getOutputStream();
                ImageIO.write(image, "jpg", out);
                out.close();

                return Response.ok().build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("BAD REQUEST").build();
        } catch (Exception ex) {
            error.put("message", getExceptionDump(ex));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }

    private boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        return folder.delete();
    }

    public FileDirectoryEntity persistFile(FileReq fileReq, Long id, String subFolder) throws NoSuchAlgorithmException {
        boolean isImage = fileReq.getType().contains("image/");
        String originalName = fileReq.getName();
        String pathname = tempDir + id + "/" + originalName;
        File file = new File(pathname);

        FileDirectoryEntity newFile = new FileDirectoryEntity();
        newFile.setCreatedAt(new Timestamp(new Date().getTime()));
        newFile.setFileName(originalName);
        newFile.setImage(isImage);
        newFile.setFileSize(fileReq.getSize());
        String salt = getShaHash("" + ThreadLocalRandom.current().nextLong());
        newFile.setSalt(salt);
        newFile.setOriginalName(originalName);
        String fileName = salt + originalName;
        String hashedFilename = getShaHash(fileName);
        newFile.setFileName(hashedFilename);

        if (isImage) {
            String imageDir = "../docroot" + subFolder;
            boolean mkdirs = new File(imageDir).mkdirs();
            System.out.println("MAKE DIR " + mkdirs);
            String imagePath = imageDir + "/" + hashedFilename;
            file.renameTo(new File(imagePath));
            deleteFolder(new File(tempDir + id));
            newFile.setDirectory(subFolder + "/" + hashedFilename);
        } else {
            String fileDir = storageDir + subFolder;
            new File(fileDir).mkdirs();
            String filePath = fileDir + "/" + hashedFilename;
            file.renameTo(new File(filePath));
            deleteFolder(new File(tempDir + id));
            newFile.setDirectory(subFolder + "/" + originalName);
        }

        fileDirectorySessionLocal.createFile(newFile);
        return newFile;
    }

    private String getShaHash(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private UserTransaction lookupUserTransaction() {
        try {
            javax.naming.Context c = new InitialContext();
            return (UserTransaction) c.lookup("java:comp/UserTransaction");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Transaction Manager Not Found Exception", ne);
            throw new RuntimeException(ne);
        }
    }

    private StoreSessionBeanLocal lookupStoreSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (StoreSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/StoreSessionBean!session.StoreSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private OrderDishSessionBeanLocal lookupOrderDishSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (OrderDishSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/OrderDishSessionBean!session.OrderDishSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerOrderSessionBeanLocal lookupCustomerOrderSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CustomerOrderSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/CustomerOrderSessionBean!session.CustomerOrderSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private DishSessionBeanLocal lookupDishSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DishSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/DishSessionBean!session.DishSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CanteenSessionBeanLocal lookupCanteenSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CanteenSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/CanteenSessionBean!session.CanteenSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
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

    private FileDirectorySessionLocal lookupFileDirectorySessionLocal() {
        try {
            Context c = new InitialContext();
            return (FileDirectorySessionLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/FileDirectorySession!session.FileDirectorySessionLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private DishTypeSessionBeanLocal lookupDishTypeSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DishTypeSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/DishTypeSessionBean!session.DishTypeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerOrderTypeSessionBeanLocal lookupCustomerOrderTypeSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CustomerOrderTypeSessionBeanLocal) c.lookup("java:global/Qoodie/Qoodie-ejb/CustomerOrderTypeSessionBean!session.CustomerOrderTypeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
