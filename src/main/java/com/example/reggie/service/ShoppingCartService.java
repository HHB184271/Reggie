package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author HHB
 * @Date: 2022/7/9 14:33
 * @Description: TODO
 * @Version 1.0
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    List<ShoppingCart> getList();




}
