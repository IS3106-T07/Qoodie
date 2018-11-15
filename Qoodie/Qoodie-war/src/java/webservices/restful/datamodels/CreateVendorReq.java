package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "createVendorReq", propOrder = {
        "emailAddress",
        "password",
        "storeName",
        "canteen",
})
public class CreateVendorReq {
    private String emailAddress;
    private String password;
    private String storeName;
    private Long canteen;

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

    public Long getCanteen() {
        return canteen;
    }

    public void setCanteen(Long canteen) {
        this.canteen = canteen;
    }
}
