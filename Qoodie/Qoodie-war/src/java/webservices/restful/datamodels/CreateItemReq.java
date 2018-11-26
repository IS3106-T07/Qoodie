package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "createItemReq", propOrder = {
        "id",
        "name",
        "description",
        "price",
        "file",
        "fileId",
        "storeId",
        "dishTypeId",
        "vendorId",
})
public class CreateItemReq {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private FileReq file;
    private Long fileId;
    private Integer storeId;
    private Long dishTypeId;
    private Long vendorId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public FileReq getFile() {
        return file;
    }

    public void setFile(FileReq file) {
        this.file = file;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDishTypeId() {
        return dishTypeId;
    }

    public void setDishTypeId(Long dishTypeId) {
        this.dishTypeId = dishTypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
