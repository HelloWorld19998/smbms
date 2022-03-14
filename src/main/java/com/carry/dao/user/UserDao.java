package com.carry.dao.user;

import com.carry.pojo.Role;
import com.carry.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    //得到要登录的用户
    public User getLoginUser(Connection connection, String userCode) throws SQLException;

    //修改当前用户密码
    public int updatePwd(Connection connection,int id,String password) throws SQLException;

    //查询用户总数
    public int getUserCount(Connection connection,String username, int userRole) throws SQLException;

    //获取用户列表  通过条件查询userList
    public List<User> getUserList(Connection connection,String userName, int userRole, int currentPageNo, int pageSize)throws Exception;

    //增加用户信息
    public int add(Connection connection, User user) throws SQLException;

    //通过userId删除user
    public int deleteUserById(Connection connection,Integer delId) throws SQLException;

    //通过userId获取user
    public User getUserById(Connection connection,String id) throws SQLException;

    //修改用户信息
    public int modify(Connection connection,User user) throws SQLException;

}
