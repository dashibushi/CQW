package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品接口")
    public Result SaveWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //删除CategoryId分类的缓存数据
        redisTemplate.delete("dish_" + dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询接口")
    public Result<PageResult> page(DishPageQueryDTO  dishPageQueryDTO){
        log.info("菜品分页查询：{}");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品接口")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);

        //删除全部的缓存数据
        deleteCache("dish_*");

        return Result.success();
    }

    /**
     * 根据Id查询菜品和对应的口味数据
     */
    @GetMapping("/{id}")
    @ApiOperation("根据Id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /**
     * 根据categoryId查询菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据categoryId查询菜品")
    public Result<List<DishVO>> listcategoryId(@RequestParam List< Long> categoryId){
        log.info("根据categoryId查询菜品：{}", categoryId);
        List<DishVO> dishVOList = dishService.listcategoryId(categoryId);
        return Result.success(dishVOList);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    @ApiOperation("修改菜品接口")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.update(dishDTO);

        //删除全部的缓存数据
        deleteCache("dish_*");

        return Result.success();
    }
    /**
     * 批量起售停售
     */
    @PostMapping("/status/{status}")
    @ApiOperation("批量起售停售接口")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("批量起售停售：{}{}", status, id);
        dishService.startOrStop(status, id);

        //删除全部的缓存数据
        deleteCache("dish_*");

        return Result.success();
    }

    /**
     * 删除全部缓存的方法
     */
    private void deleteCache(String  key ){
        Set keys = redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }

}
