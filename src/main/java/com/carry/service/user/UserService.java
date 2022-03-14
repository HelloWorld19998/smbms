package com.carry.service.user;

import com.carry.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode,String password);
    //根据用户id修改密码
    public boolean updatePwd(int id,String pwd);
    //查询记录数
    public int getUserCount(String username,int userRole);
    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    //增加用户
    public boolean add(User user);

    //通过userCode查询User
    public User selectUserCodeExist(String userCode);

    //通过userId删除user
    public boolean deleteUserById(Integer delId);

    //通过userId获取user信息
    public User getUserById(String id);

    //修改用户信息
    public boolean modify(User user);
}
