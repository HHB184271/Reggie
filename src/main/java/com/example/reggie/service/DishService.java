package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDTO;
import com.example.reggie.entity.Dish;

import java.util.List;

/**
 * @Author HHB
 * @Date: 2022/7/6 21:53
 * @Description: TODO
 * @Version 1.0
 */
public interface DishService extends IService<Dish> {


    void saveWithFlavor(DishDTO dishDTO);

    DishDTO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    void updateStatus(List<Long> ids);

    void updateStatus1(List<Long> ids);

    void removeDish(List<Long> ids);
}
