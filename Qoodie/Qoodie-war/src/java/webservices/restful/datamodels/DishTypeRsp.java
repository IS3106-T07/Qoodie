package webservices.restful.datamodels;

import entity.DishType;

public class DishTypeRsp {
    private Long id;
    private String name;

    public DishTypeRsp(){}

    public DishTypeRsp(DishType dishType) {
        setId(dishType.getId());
        setName(dishType.getName());
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
}
