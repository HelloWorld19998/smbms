package com.carry.service.bill;

import com.carry.dao.BaseDao;
import com.carry.dao.bill.BillDao;
import com.carry.dao.bill.BillDaoImpl;
import com.carry.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BillServiceImpl implements BillService{
    private BillDao billDao;
    public BillServiceImpl(){
        billDao = new BillDaoImpl();
    }

    // 通过查询条件获取供应商列表-模糊查询-getBillList
    public List<Bill> getBillList(Bill bill) {
        Connection connection = null;
        List<Bill> billList = null;
        try {
            connection = BaseDao.getConnection();
            billList = billDao.getBillList(connection,bill);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return billList;
    }

    //添加用户
    public boolean add(Bill bill){
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int updateRows = billDao.add(connection,bill);
            connection.commit();
            if (updateRows > 0){
                flag = true;
                System.out.println("add bill success!");
            } else {
                System.out.println("add bill failed!");
            }
        } catch (SQLException e) {
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

    //通过delId删除Bill
    public boolean deleteBillById(String delId) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int updateRows = billDao.deleteBillById(connection,delId);
            connection.commit();
            if (updateRows > 0){
                flag = true;
                System.out.println("delete bill success!");
            }else {
                System.out.println("delete bill failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    //通过billId获取Bill
    public Bill getBillById(String id) {
        Connection connection = null;
        Bill bill = null;
        try {
            connection = BaseDao.getConnection();
            bill = billDao.getBillById(connection,id);
        } catch (SQLException e) {
            e.printStackTrace();
            bill = null;
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return bill;
    }

    //修改订单信息
    public boolean modify(Bill bill) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务
            int updateRows = billDao.modify(connection,bill);
            connection.commit();
            if (updateRows > 0){
                flag = true;
                System.out.println("modify bill success!");
            } else {
                System.out.println("modify bill failed!");
            }
        } catch (SQLException e) {
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
