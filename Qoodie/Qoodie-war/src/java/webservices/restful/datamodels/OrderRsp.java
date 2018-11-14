package webservices.restful.datamodels;

import entity.OrderDish;

public class OrderRsp {
    private Long id;
    private String dishName;
    private Integer amount;

    public OrderRsp() {}

    public OrderRsp(OrderDish order) {
        setId(order.getId());
        setAmount(order.getAmount());
        setDishName(order.getDish().getName());
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
}
