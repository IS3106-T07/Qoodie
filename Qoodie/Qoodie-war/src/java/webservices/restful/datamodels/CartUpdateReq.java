package webservices.restful.datamodels;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "cartUpdateReq", propOrder = {
        "customerId",
        "dishId",
        "quantity",
})
public class CartUpdateReq {
    private Long customerId;
    private Long dishId;
    private Integer quantity;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
