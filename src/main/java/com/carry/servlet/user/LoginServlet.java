package com.carry.servlet.user;

import com.carry.pojo.User;
import com.carry.service.user.UserService;
import com.carry.service.user.UserServiceImpl;
import com.carry.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("LoginServlet--start...");

        //获取登录界面login.jsp传来的用户名和密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //和数据库中的密码进行对比，调用业务层
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        if (user!=null && userPassword.equals(user.getUserPassword())){//有这个人，可以登陆；并判断前端传来的密码是否和数据库的密码一致
            //将用户的信息放到Session中；
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }else {//查无此人，无法登陆
            //转发回登录界面，顺带提示，用户名或密码错误
            req.setAttribute("error","用户名或者密码错误！");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
