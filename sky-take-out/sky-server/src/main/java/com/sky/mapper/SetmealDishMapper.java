package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    List<Long> getSeteamlDsihIds(List<Long> ids);
}
