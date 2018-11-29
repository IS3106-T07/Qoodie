package webservices.restful.datamodels;

import entity.Dish;
import entity.OrderDish;

public class OrderRsp {
    private Long id;
    private String dishName;
    private Integer amount;
    private Double subtotal;
    private String status;
    private Long created;

    public OrderRsp() {}

    public OrderRsp(OrderDish order) {
        setId(order.getId());
        Integer amount = order.getAmount();
        setAmount(amount);
        Dish dish = order.getDish();
        setDishName(dish.getName());
        setStatus(order.getOrderDishStatusEnum().toString());
        setSubtotal(dish.getPrice() * amount);
        setCreated(order.getCustomerOrder().getCreated().getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }
}
