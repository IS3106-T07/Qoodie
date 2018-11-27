package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "updateBookmarkReq", propOrder = {
        "customerId",
        "bookmarkString",
})
public class UpdateBookmarkReq {
    private Long customerId;
    private String bookmarkString;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getBookmarkString() {
        return bookmarkString;
    }

    public void setBookmarkString(String bookmarkString) {
        this.bookmarkString = bookmarkString;
    }
}
