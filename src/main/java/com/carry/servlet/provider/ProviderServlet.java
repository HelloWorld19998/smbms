package com.carry.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.carry.pojo.Provider;
import com.carry.pojo.User;
import com.carry.service.provider.ProviderService;
import com.carry.service.provider.ProviderServiceImpl;
import com.carry.util.Constants;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProviderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        System.out.println("method-->"+method);
        if (method.equals("query")&&method!=null) {
            this.query(req,resp);
        } else if (method.equals("add")&&method!=null) {
            this.add(req,resp);
        }  else if (method.equals("delprovider")&&method!=null) {
            this.delProvider(req,resp);
        } else if (method.equals("view")&&method!=null) {
            this.getProviderById(req,resp,"providerview.jsp");
        } else if (method.equals("modify")&&method!=null) {
            this.getProviderById(req,resp,"providermodify.jsp");
        } else if (method.equals("modifysave")&&method!=null) {
            this.modify(req,resp);
        }
    }

    //通过供应商名称、编码获取供应商列表-模糊查询-providerList
    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryProName = req.getParameter("queryProName");
        String queryProCode = req.getParameter("queryProCode");
        if (StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if (StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<Provider>();
        ProviderService providerService = new ProviderServiceImpl();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        req.setAttribute("providerList", providerList);
        req.setAttribute("queryProName", queryProName);
        req.setAttribute("queryProCode", queryProCode);
        req.getRequestDispatcher("providerlist.jsp").forward(req, resp);
    }

    //增加供应商
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setCreationDate(new Date());
        provider.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        ProviderService providerService = new ProviderServiceImpl();
        if(providerService.add(provider)){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        } else {
            req.getRequestDispatcher("provideradd.jsp").forward(req,resp);
        }
    }

    //通过proId删除Provider
    public void delProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("proid");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(id)){
            ProviderService providerService = new ProviderServiceImpl();
            int flag = providerService.deleteProviderById(id);

            if (flag == 0){//删除成功
                resultMap.put("delResult","true");
            } else if (flag == -1){//删除失败
                resultMap.put("delResult","false");
            } else if (flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult",String.valueOf(flag));
            }
        } else {
            resultMap.put("delResult", "notexit");
        }
        //将resultMap转化为JSON形式输出
        resp.setContentType("application/json");//配置上下文的输出类型
        PrintWriter outPrintWriter = resp.getWriter();//从response对象中获取往外输出的writer对象
        outPrintWriter.write(JSONArray.toJSONString(resultMap));//把resultMap转为json字符串 输出
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    //通过proId获取Provider
    public void getProviderById(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException {
        String id = req.getParameter("proid");
        if (!StringUtils.isNullOrEmpty(id)){
            ProviderService providerService = new ProviderServiceImpl();
            Provider provider = null;
            provider = providerService.getProviderById(id);
            req.setAttribute("provider",provider);
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }

    //修改用户信息
    public void modify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        String id = req.getParameter("id");

        Provider provider = new Provider();
        provider.setId(Integer.valueOf(id));
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        ProviderService providerService = new ProviderServiceImpl();
        if (providerService.modify(provider)){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        } else {
            req.getRequestDispatcher("providermodify.jsp").forward(req,resp);
        }


    }

}
