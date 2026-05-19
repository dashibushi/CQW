package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.models.parameters.HeaderParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;




    /**
     * 根据id查询套餐
     * @param ids
     * @return
     */
    @Override
    public SetmealVO getById(Long ids) {
        Setmeal setmeal = setmealMapper.getById(ids);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealMapper.getDishItemById(ids));
        return setmealVO;
    }



    private static final Logger log = LoggerFactory.getLogger(SetmealServiceImpl.class);

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealVO setmealDTO) {
        log.info("套餐数据：{}", setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setCreateUser(BaseContext.getCurrentId());
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmealMapper.save(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.stream()
                    .filter(Objects::nonNull)
                    .forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
            setmealMapper.insertBatch(setmealDishes);
        }
    }


    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开启分页插件
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //调用mapper分页查询
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        //获取分页结果
        Long total = page.getTotal();
        List<SetmealVO> records = page.getResult();

        log.info("分页查询结果：{}{}",records, total);
        //设置分页结果
        return new PageResult(total, records);

    }



    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        setmealMapper.delete(ids);
        setmealMapper.deleteDishItemById(ids);
    }

    /**
     * 修改套餐
     * @param setmealVO
     */
    @Override
    public void update(SetmealVO setmealVO) {
        //获取套餐id
        Long id = setmealVO.getId();
        List<Long> dishIds = new ArrayList<>();
        dishIds.add( id);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO, setmeal);
        //先删除套餐,调用删除的 方法
        delete(dishIds);

        //再保存
        save(setmealVO);
    }

    /**
     * 起售停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmealMapper.updateStatus(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
