package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * @param dishFlavors
     */
    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<DishFlavor> dishFlavors);


    /**
     * 根据菜品id删除菜品口味数据
     * @param dishIds
     * @return
     */
   // @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByids(List< Long> dishIds);

    /**
     * 根据id查询菜品数据
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
