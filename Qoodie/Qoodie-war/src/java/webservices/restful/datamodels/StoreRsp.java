package webservices.restful.datamodels;

import entity.CuisineType;
import entity.Dish;
import entity.Store;

import java.util.ArrayList;
import java.util.List;

public class StoreRsp {
    private Long id;
    private String name;
    private Long vendorId;
    private String vendorEmail;
    private List<DishRsp> dishes;
    private String cuisineType;
    private Long cuisineTypeId;

    public StoreRsp() {}

    public StoreRsp(Store store) {
        setId(store.getId());
        setName(store.getName());
        List<Dish> dishes = store.getDishes();
        List<DishRsp> dishRsps = new ArrayList<>();
        for (Dish dish : dishes) {
            dishRsps.add(new DishRsp(dish));
        }
        setDishes(dishRsps);
        CuisineType cuisineType = store.getCuisineType();
        setCuisineType(cuisineType == null ? null : cuisineType.getName());
        setCuisineTypeId(cuisineType == null ? null : cuisineType.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public List<DishRsp> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishRsp> dishes) {
        this.dishes = dishes;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public Long getCuisineTypeId() {
        return cuisineTypeId;
    }

    public void setCuisineTypeId(Long cuisineTypeId) {
        this.cuisineTypeId = cuisineTypeId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}
