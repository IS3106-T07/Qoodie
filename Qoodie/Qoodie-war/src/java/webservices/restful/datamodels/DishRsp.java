package webservices.restful.datamodels;

import entity.Dish;
import entity.DishType;
import entity.FileDirectoryEntity;

public class DishRsp {
    private Long id;
    private String name;
    private Boolean isAvailable;
    private Double price;
    private String description;
    private String dishType;
    private String photoDir;

    public DishRsp() {}

    public DishRsp(Dish dish) {
        setId(dish.getId());
        setName(dish.getName());
        setAvailable(dish.getIsAvailable());
        setPrice(dish.getPrice());
        setDescription(dish.getDescription());
        DishType dishType = dish.getDishType();
        if (dishType != null) setDishType(dishType.getName());
        FileDirectoryEntity fileDirectoryEntity = dish.getFileDirectoryEntity();
        if (fileDirectoryEntity != null) setPhotoDir(fileDirectoryEntity.getDirectory());
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

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDishType() {
        return dishType;
    }

    public void setDishType(String dishType) {
        this.dishType = dishType;
    }

    public String getPhotoDir() {
        return photoDir;
    }

    public void setPhotoDir(String photoDir) {
        this.photoDir = photoDir;
    }
}
