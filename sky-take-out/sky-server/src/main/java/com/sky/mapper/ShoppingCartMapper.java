package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     *查询购物车
     * @param shoppingCartDTO
     */
    @Select("select id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time from shopping_cart where dish_id = #{dishId} or setmeal_id = #{setmealId}")
    ShoppingCart getByDishIdOrSetmealId(ShoppingCartDTO shoppingCartDTO);

    /**
     * 购物车菜品或套餐数量加一
     * @param shoppingCart
     */

    void update(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 动态查询购物车数据
     * @param userId
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     * @param userId
     */
    @Select("delete from shopping_cart where user_id = #{userId}")
    void delete(Long userId);
}
