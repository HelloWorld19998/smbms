package com.carry.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.carry.pojo.Role;
import com.carry.pojo.User;
import com.carry.service.role.RoleService;
import com.carry.service.role.RoleServiceImpl;
import com.carry.service.user.UserService;
import com.carry.service.user.UserServiceImpl;
import com.carry.util.Constants;
import com.carry.util.PageSupport;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carry.util.Constants.USER_SESSION;

public class UserServlet extends HttpServlet {

    //将Servlet复用
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        System.out.println("method-->"+method);
        if (method.equals("savepwd")&&method!=null) {
            this.updatePwd(req,resp);
        }else if (method.equals("pwdmodify")&&method!=null){
            this.pwdModify(req,resp);
        }else if (method.equals("query")&&method!=null){
            this.query(req,resp);
        }else if (method.equals("add")&&method!=null){
            this.add(req,resp);
        }else if (method.equals("getrolelist")&&method!=null){
            this.getRoleList(req,resp);
        }else if (method.equals("ucexist")&&method!=null){
            this.userCodeExist(req,resp);
        }else if (method.equals("deluser")&&method!=null){
            this.delUser(req,resp);
        }else if (method.equals("view")&&method!=null){
            this.getUserById(req,resp,"userview.jsp");
        }else if (method.equals("modify")&&method!=null){
            this.getUserById(req,resp,"usermodify.jsp");
        }else if (method.equals("modifyexe")&&method!=null){
            this.modify(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    //重点、难点
    public void query(HttpServletRequest req, HttpServletResponse resp){
        //查询用户列表

        //从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        //第一次走这个请求，一定是第一页，页面大小固定的
        int pageSize = Constants.PageSize;//可以把这个写到配置文件中，方便后期修改
        int currentPageNo = 1;

        if (queryUserName == null){
            queryUserName = "";
        }
        if (temp!=null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);//给查询赋值! 0,1,2,3
        }
        if (pageIndex!=null){
            currentPageNo = Integer.parseInt(pageIndex);
        }

        //获取用户的总数（分页： 上一页、下一页）
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = ((int)(totalCount/pageSize))+1;

        //控制页面首页和尾页
        //如果页面要小于1，就显示第一页的东西
        if (currentPageNo < 1){
            currentPageNo = 1;
        }else if (currentPageNo>totalPageCount){//如果页面大于总页面数，显示最后一页
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp){
        //从Session里面拿id
        Object attribute = req.getSession().getAttribute(USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        if(attribute!= null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User)attribute).getId(),newpassword);
            if(flag){
                req.setAttribute("message","修改密码成功，请退出，用新密码登录");
                //密码修改成功，移除当前Session
                req.getSession().removeAttribute(USER_SESSION);
            }else {
                req.setAttribute("message","修改密码失败");
            }
        }else {
            req.setAttribute("message","新密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码，session中有用户的密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        //从Session里面拿ID；
        Object attribute = req.getSession().getAttribute(USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //万能的Map，结果集
        Map<String, String> resultMap = new HashMap<String, String>();
        if(attribute==null){//Session失效了，session过期了
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){//输入的密码为空
            resultMap.put("result","error");
        }else {
            String userPassword = ((User) attribute).getUserPassword();//Session中用户的密码
            if (oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else {
                resultMap.put("result","false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JSONArray 阿里巴巴的JSON工具类，转换格式
            /*
            resultMap = {"result","sessionerror","result","error"}
            JSON = {key:value}
             */
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //增加用户
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        System.out.println("add()================");
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-DD").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if (userService.add(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else {
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }
    }


    public void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转为JSON格式输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //ajax后台验证userCode是否已存在    /jsp/user.do?method=ucexist
    public void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //判断用户账户是否可用
        String userCode = req.getParameter("userCode");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("");
            resultMap.put("userCode","exist");
        }else {
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if (user != null){
                resultMap.put("userCode","exist");
            }else {
                resultMap.put("userCode","unexist");
            }
        }
        //将resultMap转化为JSON形式输出
        resp.setContentType("application/json");//配置上下文的输出类型
        PrintWriter outPrintWriter = resp.getWriter();//从response对象中获取往外输出的writer对象
        outPrintWriter.write(JSONArray.toJSONString(resultMap));//把resultMap转为json字符串 输出
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    //根据userId删除user     "/jsp/user.do?method=deluser"
    public void delUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("uid");
        Integer delId = 0;
        try {
            delId = Integer.parseInt(id);
        } catch (Exception e){
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();

        if (delId <= 0){
            resultMap.put("delResult","notexist");
        }else {
            UserService userService = new UserServiceImpl();
            if (userService.deleteUserById(delId)){
                resultMap.put("delResult","true");
            }else {
                resultMap.put("delResult","false");
            }
        }
        //将resultMap转化为JSON形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    public void getUserById(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException {
        String id = req.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            req.setAttribute("user",user);
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }

    public void modify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();

        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-DD").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)req.getSession().getAttribute(USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if(userService.modify(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else {
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        }
    }
}
