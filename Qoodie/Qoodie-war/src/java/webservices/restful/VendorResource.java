/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import entity.Store;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import session.StoreSessionBeanLocal;
import webservices.restful.datamodels.CreateItemReq;
import webservices.restful.datamodels.CreateVendorReq;
import webservices.restful.datamodels.StoreRsp;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webservices.restful.util.ResponseHelper.getExceptionDump;

/**
 *
 * @author sinhv
 */
@Path("vendors")
public class VendorResource {
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
    public Response uploadImage(@FormDataParam("filepond") InputStream is,
                                @FormDataParam("filepond")FormDataContentDisposition fileDetails) {
        try {
            String fileName = fileDetails.getFileName();
            System.out.println("Uploading File " + fileName + " To Temp");
            long tmpId = new Date().getTime();
            String path = tempDir + tmpId;
            System.out.println(path);
            File file = new File(path);
            if (!file.mkdir()) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            writeToFile(is, path + "/" + fileName);
            return  Response.status(Response.Status.OK).entity(tmpId).build();
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
            userTransaction.begin();
            String _dir = persistDir + "dishes/";
            new File(_dir).mkdirs();
            String _tempDir = tempDir + createItemReq.getFile();
            File oldDir = new File(_tempDir);
            System.out.println("Old Dir " + _tempDir);
            File file = oldDir.listFiles()[0];
            String newDir = _dir + createItemReq.getFile() + "_" + file.getName();
            System.out.println("New Dir " + newDir);
            boolean moved = file.renameTo(new File(newDir));
            if (moved) {
                System.out.println("Delete File " + _tempDir + " in Temp");
                deleteFolder(oldDir);
                userTransaction.commit();
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            userTransaction.setRollbackOnly();
            return  Response.status(Response.Status.NOT_ACCEPTABLE).entity("CANNOT MOVE FILE").build();
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
    @Path("revertImage")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response revertImage(String tmpId) {
        try {
            System.out.println("Delete File " + tmpId + " in Temp");
            String path = tempDir + tmpId;
            System.out.println(path);
            File file = new File(path);
            boolean deleteResult = deleteFolder(file);
            if (!deleteResult) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("File Doesn't Exist").build();
            return  Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(getExceptionDump(ex)).build();
        }
    }

    @POST
    @Path("createVendor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVendor(CreateVendorReq createVendorReq) {
        try {
            userTransaction.begin();
            String username = createVendorReq.getUsername().toLowerCase();
            if (username.contains(" ")) {
                userTransaction.setRollbackOnly();
                error.put("message", "Username contains spaces");
                return  Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            if (!storeSessionBeanLocal.checkVendorUserName(username)) {
                userTransaction.setRollbackOnly();
                error.put("message", "User Exists");
                return  Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();
            }
            Store store = new Store();
            store.setVendorUsername(username);
            store.setVendorEmail(createVendorReq.getEmailAddress());
            store.setPassword(createVendorReq.getPassword());
            store.setName(createVendorReq.getStoreName());
            storeSessionBeanLocal.createStore(store);
            userTransaction.commit();
            StoreRsp response = new StoreRsp(store);
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
}
