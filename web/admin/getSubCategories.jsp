<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.wanggong232.wangyifei.shopping02.dao.ProductDao" %>
<%@ page import="com.wanggong232.wangyifei.shopping02.model.SubCategory" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.gson.Gson" %>
<%
    int categoryId = Integer.parseInt(request.getParameter("categoryId"));
    ProductDao productDao = new ProductDao();
    List<SubCategory> subCategories = productDao.getSubCategoriesByCategoryId(categoryId);

    Gson gson = new Gson();
    String json = gson.toJson(subCategories);
    out.print(json);
%>

<%--不用重新新建。--%>