package com.sky.controller.admin;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理接口")
public class StemealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealVO setmealDTO){
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param ids
     * @return
     */
    @GetMapping("/{ids}")
    @ApiOperation("查询套餐接口")
    public Result<SetmealVO> getById(@PathVariable Long ids){
        SetmealVO setmealVO = setmealService.getById(ids);
        return Result.success(setmealVO);
    }

    /**
     * 分页查询套餐
     * @param
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);

    }


    /**
     * 修改套餐
     * @param
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealVO setmealVO){
        setmealService.update(setmealVO);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐接口")
    public Result delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return Result.success();
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售接口")
    public Result startOrStop(@PathVariable Integer status, Long id){
        setmealService.startOrStop(status,id);
        return Result.success();
    }

}
