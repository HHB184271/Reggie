package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.DishDTO;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.DishFlavor;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author HHB
 * @Date: 2022/7/6 21:54
 * @Description: TODO
 * @Version 1.0
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    /***
     * 新增菜品同时保存口味
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        //保存菜品基本信息到dish表
        this.save(dishDTO);
        //获取菜品ID
        Long dishId = dishDTO.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        log.info("{}",flavors);
        //菜品口味
        for (DishFlavor dishFlavor: flavors){
            dishFlavor.setDishId(dishId);
        }
        log.info("{}",flavors);
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDTO dishDto = new DishDTO();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /***
     * 修改菜品信息
     * @param dishDto
     */

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor dishFlavor: flavors){
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void updateStatus(List<Long> ids) {

        for (Long id: ids) {
            Dish dish = this.getById(id);
            dish.setStatus(1);
            this.updateById(dish);
        }

    }
    @Override
    public void updateStatus1(List<Long> ids) {

        for (Long id: ids) {
            Dish dish = this.getById(id);
            dish.setStatus(0);
            this.updateById(dish);
        }

    }

    @Override
    public void removeDish(List<Long> ids) {
        for (Long id :ids){
            this.removeById(id);
        }
    }

}
