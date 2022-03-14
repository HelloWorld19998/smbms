package com.carry.service.user;

import com.carry.dao.BaseDao;
import com.carry.dao.user.UserDao;
import com.carry.dao.user.UserDaoImpl;
import com.carry.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService{

    //业务层都会调用dao层，所以我们要引入Dao层
    private UserDao userDao;

    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体的数据库操作
            user = userDao.getLoginUser(connection,userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        boolean flag = false;
        //修改密码
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection,id,pwd)>0){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    //查询记录数
    public int getUserCount(String username, int userRole) {
         Connection connection = null;
         int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection,username,userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return count;
    }

    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {

        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName--->"+queryUserName);
        System.out.println("queryUserRole--->"+queryUserRole);
        System.out.println("currentPageNo--->"+currentPageNo);
        System.out.println("pageSize--->"+pageSize);

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return userList;
    }

    //增加用户
    public boolean add(User user){
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.add(connection,user);
            connection.commit();
            if (updateRows > 0){
                flag = true;
                System.out.println("add user success!");
            }else {
                System.out.println("add user failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("=====rollback======");
                connection.rollback();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    //通过userCode查询User
    public User selectUserCodeExist(String userCode){
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection,userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    //通过userId删除user
    public boolean deleteUserById(Integer delId) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.deleteUserById(connection,delId);
            connection.commit();
            if (updateRows > 0){
                flag = true;
                System.out.println("delete user success!");
            }else {
                System.out.println("delete user failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("=====rollback=====");
                connection.rollback();//回滚
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    //通过userId获取user信息
    public User getUserById(String id) {
        User user = null;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection,id);
        } catch (SQLException e) {
            e.printStackTrace();
            user = null;
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    //修改用户信息
    public boolean modify(User user){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启事务
            int updateRows = userDao.modify(connection, user);
            connection.commit();//事务提交
            if (updateRows > 0){
                System.out.println("modify user success!");
                flag = true;
            }else {
                System.out.println("modify user failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("=====rollback=====");
                connection.rollback();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return flag;
    }
}
