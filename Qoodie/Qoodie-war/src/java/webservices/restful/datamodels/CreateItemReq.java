package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "createItemReq", propOrder = {
        "name",
        "price",
        "file",
        "storeId",
        "dishType",
})
public class CreateItemReq {
    private String name;
    private Double price;
    private String file;
    private Integer storeId;
    private String dishType;

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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getDishType() {
        return dishType;
    }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }
}
