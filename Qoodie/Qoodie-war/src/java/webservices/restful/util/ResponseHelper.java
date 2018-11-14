/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.restful.util;

/**
 *
 * @author sinhv
 */
public class ResponseHelper {
    public static String getExceptionDump(Exception ex) {
        StringBuilder result = new StringBuilder();
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (result.length() > 0)
                result.append("Caused by: ");
            result.append(cause.getClass().getName());
            result.append(": ");
            result.append(cause.getMessage());
            result.append("\n");
            for (StackTraceElement element: cause.getStackTrace()) {
                result.append("\tat ");
                result.append(element.getMethodName());
                result.append("(");
                result.append(element.getFileName());
                result.append(":");
                result.append(element.getLineNumber());
                result.append(")\n");
            }
        }
        return result.toString();
    }
}
