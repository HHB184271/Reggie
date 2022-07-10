package com.example.reggie.filter;


import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @Author HHB
 * @Date: 2022/7/5 11:39
 * @Description: 过滤器->检查是否进行登录
 * @Version 1.0
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //使用匹配器支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;


        //获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求： {}", requestURI);

        //定义不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //判断请求路径是否需要处理
        Boolean check = check(urls, requestURI);

        //不处理便放行
        if (check){
            log.info("本次请求不需要处理：{}",requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //判断登录状态,已登录直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户ID为：{}",request.getSession().getAttribute("employee"));
            Long empID = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empID);
            filterChain.doFilter(request, response);
            return;
        }

        //判断登录状态,已登录直接放行[移动端]
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户ID为：{}",request.getSession().getAttribute("user"));
            Long userID = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userID);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        //未登录则返回未登录结果，通过输出流向客户端返回响应的数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }
    public Boolean check(String[] urls, String requestURL){
        for (String url :urls){
            Boolean match = PATH_MATCHER.match(url,requestURL);
            if (match){
                return true;
            }
        }
        return false;

    }
}
