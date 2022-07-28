package com.guanghe.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.guanghe.takeout.common.BaseContext;
import com.guanghe.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器 检查用户是否已经登录
 *
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获得本次请求url
        String requestURL = request.getRequestURI();
        //定义不需要处理的请求路径,只拦截业务类的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURL);
        //3.如果不需要处理直接放行
        if (check){
//            log.info("本次请求{}不需要处理",requestURL);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1.判断电脑端用户登录状态，如果已登录，则放行
        if (request.getSession().getAttribute("employee")!=null){
//            log.info("用户已登录,用户id为:{}",request.getSession().getAttribute("employee"));

            //设置当前登录用户的id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);//将empId变量保存到当前线程中

            filterChain.doFilter(request,response);
            return;
        }
        //4-2.判断移动端用户登录状态，如果已登录，则放行
        if (request.getSession().getAttribute("user")!=null){
//            log.info("用户已登录,用户id为:{}",request.getSession().getAttribute("employee"));

            //设置当前登录用户的id
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);//将userId变量保存到当前线程中

            filterChain.doFilter(request,response);
            return;
        }
        //5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("拦截到请求：{}",requestURL);
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String[] urls,String requestURL){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }
}
