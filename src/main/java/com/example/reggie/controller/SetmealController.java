package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDTO;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author HHB
 * @Date: 2022/7/8 9:53
 * @Description:  套餐管理
 * @Version 1.0
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /***
     * 套餐分页查询显示
     */
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public R<Page> pageDish(int page, int pageSize, String name){

        //构造分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> SetmealDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行分页查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        /***
         * 将对象拷贝到一个合适的数据对象
         */
        //拷贝已查询过的对象pageInfo【里面有page分页里面的所有信息】
        BeanUtils.copyProperties(pageInfo,SetmealDtoPage,"records");
        //创建集合用于存储数据信息
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //将最后获取的数据集合对象传给，分页查询
        SetmealDtoPage.setRecords(list);
        return R.success(SetmealDtoPage);
    }
    /***
     * 删除套餐
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){

        setmealService.deleteSetmael(ids);

        return R.success("删除套餐成功");

    }
    /***
     * 禁用菜品
     */
    @PostMapping("/status/1")
    public R<String> updateStatus(@RequestParam List<Long> ids){
        for (Long id: ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }
        return R.success("启用成功");
    }
    /***
     * 启用菜品
     */
    @PostMapping("/status/0")
    public R<String> updateStatus2(@RequestParam List<Long> ids){
        for (Long id: ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }
        return R.success("禁用成功");
    }

    /***
     * 套餐查询
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealList(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);

    }


}
