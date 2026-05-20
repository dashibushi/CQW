package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 根据categoryId查寻菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> listcategoryId(List<Long> categoryIds) {
        List<DishVO> dishVOList = new ArrayList<>();

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Dish> dishList = dishMapper.listByCategoryIds(categoryIds);

            for (Dish dish : dishList) {
                DishVO dishVO = new DishVO();
                BeanUtils.copyProperties(dish, dishVO);

                List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dish.getId());
                dishVO.setFlavors(flavors);

                dishVOList.add(dishVO);
            }
        }
        return dishVOList;
    }
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        log.info("新增菜品和对应的口味{}",dishDTO);

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);


        //新增菜品
        dishMapper.insert(dish);

        //新增口味
        //判断口味是否为空
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()> 0){
            flavors.stream()
                    .filter(Objects::nonNull)
                    .forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据id删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {

        for (Long id : ids) {
            //判断菜品是否处于起售状态
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE)
            {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否被套餐关联
        List<Long> seteamlIds = setmealDishMapper.getSeteamlDsihIds(ids);
        log.info("菜品关联{}",seteamlIds);
        if (seteamlIds != null && seteamlIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //对菜品进行删除

            //对菜品进行删除操作
            dishMapper.deleteByIds(ids);
            //对菜品的口味进行删除
            dishFlavorMapper.deleteByids(ids);

    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        //先删除原有的菜品和口味
        List<Long> ids = new ArrayList<>();
        ids.add(dishDTO.getId());
        log.info("目标id{}",ids);
        dishFlavorMapper.deleteByids(ids);

        //再添加更改的菜品和口味
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.stream()
                    .filter(Objects::nonNull)
                    .forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 骑手停售方法
     * @param status
     * @param ids
     */
    @Override
    public void startOrStop(Integer status,Long id) {
        dishMapper.startOrStop(status,id);
    }
}
