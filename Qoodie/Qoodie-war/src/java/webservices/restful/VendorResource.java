/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import webservices.restful.util.StorageUrl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Date;
import java.util.LinkedHashMap;

import static webservices.restful.util.ResponseHelper.getExceptionDump;

/**
 *
 * @author sinhv
 */
@Path("vendors")
public class VendorResource {
    private LinkedHashMap<Object, Object> error = new LinkedHashMap<>();
    private String tempUrl = StorageUrl.url + "temp/";

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
            String path = tempUrl + tmpId;
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
    @Path("revertImage")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response revertImage(String tmpId) {
        try {
            System.out.println("Delete File " + tmpId + " in Temp");
            String path = tempUrl + tmpId;
            System.out.println(path);
            File file = new File(path);
            boolean deleteResult = deleteFolder(file);
            error.put("message", deleteResult);
            if (!deleteResult) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
            return  Response.status(Response.Status.NO_CONTENT).build();
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

    public static void main(String[] args) {
        File file = new File("./storage/temp");
        System.out.println(file.exists());
    }
}
