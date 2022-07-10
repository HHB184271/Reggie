package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDTO;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author HHB
 * @Date: 2022/7/7 13:14
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;

    /***
     * 添加菜品
     */
    @RequestMapping(method = RequestMethod.POST)
    public R<String> saveDish(@RequestBody DishDTO dishDTO){

        dishService.saveWithFlavor(dishDTO);

        return R.success("菜品添加成功");

    }

    /***
     * 菜品分页查询显示
     */
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public R<Page> pageDish(int page, int pageSize, String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDTO> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        /***
         * 将对象拷贝到一个合适的数据对象
         */
        //拷贝已查询过的对象pageInfo【里面有page分页里面的所有信息】
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //创建集合用于存储数据信息
        List<Dish> records = pageInfo.getRecords();

        List<DishDTO> list = records.stream().map((item) -> {

            DishDTO dishDto = new DishDTO();
            //将每次遍历的信息存入目标dto
            BeanUtils.copyProperties(item,dishDto);
            //获取对象中的分类ID
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象（对象包含Category的所有信息）
            Category category = categoryService.getById(categoryId);

            if(category != null){
                //获取该对象的名字->对象分类
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //返回遍历中的完整数据对象，由collect进行收集并转化为List集合传给List<DishDTO>
            return dishDto;
        }).collect(Collectors.toList());

        //将最后获取的数据集合对象传给，分页查询
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }
    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable Long id){

        DishDTO dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /***
     * 添加菜品
     */
    @RequestMapping(method = RequestMethod.PUT)
    public R<String> updateDish(@RequestBody DishDTO dishDTO){

        dishService.updateWithFlavor(dishDTO);

        return R.success("菜品添加成功");

    }

    /***
     * 禁用菜品
     */
    @PostMapping("/status/1")
    public R<String> updateStatus(@RequestParam List<Long> ids){
        dishService.updateStatus(ids);
        return R.success("启用成功");
    }
    /***
     * 启用菜品
     */
    @PostMapping("/status/0")
    public R<String> updateStatus2(@RequestParam List<Long> ids){
        dishService.updateStatus1(ids);
        return R.success("禁用成功");
    }

    /***
     * 删除菜品
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){
        dishService.removeDish(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
//        //添加条件，查询状态为1（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDTO>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDTO> dishDtoList = list.stream().map((item) -> {

            DishDTO dishDto = new DishDTO();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}

