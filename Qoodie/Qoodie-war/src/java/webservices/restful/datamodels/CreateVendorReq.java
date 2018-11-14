package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "createVendorReq", propOrder = {
        "username",
        "emailAddress",
        "password",
        "storeName",
        "canteen",
})
public class CreateVendorReq {
    private String username;
    private String emailAddress;
    private String password;
    private String storeName;
    private String canteen;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCanteen() {
        return canteen;
    }

    public void setCanteen(String canteen) {
        this.canteen = canteen;
    }
}
