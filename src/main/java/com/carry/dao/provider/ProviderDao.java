package com.carry.dao.provider;

import com.carry.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProviderDao {

    //通过供应商名称、编码获取供应商列表-模糊查询-providerList
    public List<Provider> getProviderList(Connection connection,String proName,String proCode) throws SQLException;

    //增加供应商
    public int add(Connection connection, Provider provider)throws SQLException;

    //通过proId删除Provider
    public int deleteProviderById(Connection connection, String delId)throws SQLException;

    //通过proId获取Provider
    public Provider getProviderById(Connection connection, String id)throws Exception;

    //修改用户信息
    public int modify(Connection connection, Provider provider)throws Exception;

}
