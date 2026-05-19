package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmeal
     */
    void save(Setmeal setmeal);

    /**
     * 批量插入套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据id查询套餐数据
     * @param ids
     * @return
     */
    @Select("select id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user from setmeal where id = #{id}")
    Setmeal getById(Long ids);

    /**
     * 根据id查询套餐和菜品的关联数据
     * @param ids
     * @return
     */
    @Select("select sd.name,sd.price, sd.copies from setmeal_dish sd inner join setmeal s on sd.setmeal_id = s.id where s.id = #{id}")
    List<SetmealDish> getDishItemById(Long ids);

    /**
     * 批量删除套餐
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 批量删除套餐和菜品的关联关系
     * @param ids
     */
    void deleteDishItemById(List<Long> ids);

    /**
     * 修改套餐的起售停售状态
     * @param setmeal
     */
    @Update("update setmeal set status = #{status}, update_time = #{updateTime}, update_user = #{updateUser} where id = #{id}")
    void updateStatus(Setmeal setmeal);


    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);


    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
