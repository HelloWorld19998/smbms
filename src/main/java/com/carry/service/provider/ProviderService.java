package com.carry.service.provider;

import com.carry.pojo.Provider;
import com.carry.pojo.User;

import java.util.List;

public interface ProviderService {


    //通过供应商名称、编码获取供应商列表-模糊查询-providerList
    public List<Provider> getProviderList(String proName, String proCode);

    //增加供应商
    public boolean add(Provider provider);

    //通过proId删除Provider
    public int deleteProviderById(String delId);

    //通过proId获取Provider
    public Provider getProviderById(String id);

    //修改用户信息
    public boolean modify(Provider provider);
}
