package com.carry.service.provider;

import com.carry.dao.BaseDao;
import com.carry.dao.bill.BillDao;
import com.carry.dao.bill.BillDaoImpl;
import com.carry.dao.provider.ProviderDao;
import com.carry.dao.provider.ProviderDaoImpl;
import com.carry.pojo.Provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProviderServiceImpl implements ProviderService {

    private ProviderDao providerDao;
    private BillDao billDao;
    public ProviderServiceImpl(){
        providerDao = new ProviderDaoImpl();
        billDao = new BillDaoImpl();
    }


    //通过供应商名称、编码获取供应商列表-模糊查询-providerList
    public List<Provider> getProviderList(String proName, String proCode) {
        Connection connection = null;
        List<Provider> providerList = null;
        System.out.println("query proName ---- > " + proName);
        System.out.println("query proCode ---- > " + proCode);
        try {
            connection = BaseDao.getConnection();
            providerList = providerDao.getProviderList(connection,proName,proCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return providerList;
    }

    public boolean add(Provider provider) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务
            int updateRows = providerDao.add(connection, provider);
            connection.commit();//提交事务
            if (updateRows > 0){
                flag = true;
                System.out.println("add provider success!");
            } else {
                System.out.println("add provider failed!");
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
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    /**
     * 业务：根据ID删除供应商表的数据之前，需要先去订单表里进行查询操作
     * 若订单表中无该供应商的订单数据，则可以删除
     * 若有该供应商的订单数据，则不可以删除
     * 返回值billCount
     * 1> billCount == 0  删除---1 成功 （0） 2 不成功 （-1）
     * 2> billCount > 0    不能删除 查询成功（0）查询不成功（-1）
     *
     * ---判断
     * 如果billCount = -1 失败
     * 若billCount >= 0 成功
     */
    //通过proId删除Provider
    public int deleteProviderById(String delId) {
        Connection connection = null;
        int billCount = -1;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            billCount = billDao.getBillCountByProviderId(connection,delId);//判断订单表中有无该供应商的订单数据
            if (billCount == 0){
                providerDao.deleteProviderById(connection,delId);
            }
            connection.commit();
        } catch (SQLException e) {
            billCount = -1;
            e.printStackTrace();
            try {
                System.out.println("=====rollback=====");
                connection.rollback();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } finally {
                BaseDao.closeResource(connection, null, null);
            }
        }
        return billCount;
    }

    //通过proId获取Provider
    public Provider getProviderById(String id) {
        Connection connection = null;
        Provider provider = null;
        try {
            connection = BaseDao.getConnection();
            provider = providerDao.getProviderById(connection,id);
        } catch (Exception e) {
            e.printStackTrace();
            provider = null;
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return provider;
    }

    //修改用户信息
    public boolean modify(Provider provider){
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务
            int updateRows = providerDao.modify(connection, provider);
            connection.commit();
            if (updateRows > 0){
                flag = true;
                System.out.println("modify provider success!");
            } else {
                System.out.println("modify provider failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println("=====rollback=====");
                connection.rollback();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

}
