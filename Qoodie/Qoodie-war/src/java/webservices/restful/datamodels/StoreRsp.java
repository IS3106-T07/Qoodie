package webservices.restful.datamodels;

import entity.*;

import java.util.ArrayList;
import java.util.List;

public class StoreRsp {
    private Long storeId;
    private String name;
    private Long vendorId;
    private String vendorEmail;
    private List<DishRsp> dishes;
    private String cuisineType;
    private Long cuisineTypeId;
    private String vendorName;
    private String vendorAddress;
    private String vendorBankAccountNumber;
    private String vendorPhotoDir;

    public StoreRsp() {}

    public StoreRsp(Store store) {
        setStoreId(store.getId());
        setName(store.getName());

        List<Dish> storeDishes = store.getDishes();
        List<Dish> dishes = new ArrayList<>();
        for (Dish storeDish : storeDishes) {
            if (storeDish.getIsAvailable()) dishes.add(storeDish);
            else {
                System.out.println(storeDish.getIsAvailable());
            }
        }
        System.out.println("# DISHES " + dishes.size());
        List<DishRsp> dishRsps = new ArrayList<>();
        for (Dish dish : dishes) {
            dishRsps.add(new DishRsp(dish));
        }
        setDishes(dishRsps);
        CuisineType cuisineType = store.getCuisineType();
        setCuisineType(cuisineType == null ? null : cuisineType.getName());
        setCuisineTypeId(cuisineType == null ? null : cuisineType.getId());
        Customer vendor = store.getVendor();
        setVendorEmail(vendor.getEmail());
        setVendorId(vendor.getId());
        setVendorName(vendor.getName());
        setVendorAddress(vendor.getAddress());
        setVendorBankAccountNumber(vendor.getBankAccountNumber());
        FileDirectoryEntity photo = vendor.getPhoto();
        if (photo != null) setVendorPhotoDir(photo.getDirectory());
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorBankAccountNumber() {
        return vendorBankAccountNumber;
    }

    public void setVendorBankAccountNumber(String vendorBankAccountNumber) {
        this.vendorBankAccountNumber = vendorBankAccountNumber;
    }

    public String getVendorPhotoDir() {
        return vendorPhotoDir;
    }

    public void setVendorPhotoDir(String vendorPhotoDir) {
        this.vendorPhotoDir = vendorPhotoDir;
    }
}
