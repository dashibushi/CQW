package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    //菜品id
    private Long dishId;
    //套餐id
    private Long setmealId;
    //菜品口味
    private String dishFlavor;

}
