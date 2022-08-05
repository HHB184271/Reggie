package com.example.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.Orders;
import com.example.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /***
     * 显示所有订单
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public R<Page> page(int page, int pageSize, String number){

        //构造分页器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getId,number);
        //添加排序条件(根据创建时间)
        queryWrapper.orderByDesc(Orders::getOrderTime);
        log.info("{}",pageInfo);
        //执行查询
        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }
    /***
     * 显示所有订单
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/userPage", method = RequestMethod.GET)
    public R<Page> userPage(int page, int pageSize){

        //构造分页器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件(根据创建时间)
        queryWrapper.orderByDesc(Orders::getOrderTime);
        log.info("{}",pageInfo);
        //执行查询
        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }
}