package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author sinhv
 */
@XmlRootElement
@XmlType(name = "createPaymentReq", propOrder = {
        "name",
        "type",
        "size",
})
public class FileReq {
    private String name;
    private String type;
    private Long size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
