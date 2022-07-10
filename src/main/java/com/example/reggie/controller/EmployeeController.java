package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @Author HHB
 * @Date: 2022/7/5 9:46
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /***
     * 员工登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public R<Employee> login (HttpServletRequest request, @RequestBody Employee employee){

        //1. md5加密转换
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        //2.数据库查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //封装查询条件
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp =employeeService.getOne(queryWrapper);

        //3.校验
        if (emp == null){
            return  R.error("登录失败");
        }
        if ( !emp.getPassword().equals(password)){
            return  R.error("登录失败");
        }
        if (emp.getStatus() == 0){
            return  R.error("账号禁用");
        }
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }
    /***
     * 员工登出
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public R<String> logout(HttpServletRequest request){
        //清理当前员工的在Session中保存的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /***
     * 添加员工
     *
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        log.info("新增员工的信息为：{}",employee.toString());
        //设置初始密码使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置添加时间和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户的ID
//        Long empID = (Long) request.getSession().getAttribute("employee");

        //更新表格更新人信息
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        employeeService.save(employee);
        log.info("新增员工的信息为：{}",employee.toString());
        return R.success("新增员工成功！");
    }

    /***
     * 新增员工信息
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public R<Page> page(int page,int pageSize,String name){

        log.info("page: {}, pageSize: {}, name: {}",page,pageSize,name);

        //构造分页器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        //添加排序条件(根据创建时间)
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }
    /***
     * 启用禁用员工账号
     */
    @RequestMapping(method = RequestMethod.PUT)
    public  R<String> update(@RequestBody Employee employee){

//        Long empID = (Long) request.getSession().getAttribute("employee");

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empID);

        employeeService.updateById(employee);
        return R.success("修改成功");

    }
    /***
     * 根据员工id获取信息并进行更新
     */
    @RequestMapping("/{id}")
    public R<Employee> getId(@PathVariable Long id){

        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("更新失败");
    }


}
