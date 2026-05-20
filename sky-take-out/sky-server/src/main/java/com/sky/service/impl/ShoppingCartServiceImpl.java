package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        //判断当前添加的购物车项是否在购物车中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list= shoppingCartMapper.list(shoppingCart);
        // 如果已经存在，则数量加1
        if(list != null&& list.size() > 0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        }
        else {
            // 不存在，创建新的购物车项
            ShoppingCart newCart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCartDTO, newCart);
            newCart.setUserId(userId);

            if (shoppingCartDTO.getDishId() != null) {
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());

                newCart.setName(dish.getName());
                newCart.setAmount(dish.getPrice());
                newCart.setImage(dish.getImage());

            } else if (shoppingCartDTO.getSetmealId() != null) {
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());

                newCart.setName(setmeal.getName());
                newCart.setAmount(setmeal.getPrice());
                newCart.setImage(setmeal.getImage());

            }
            newCart.setNumber(1);
            newCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(newCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> ShowShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.delete(userId);
    }
}
