<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*" %>

<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    if ("ABC".equals(username) && "MNK".equals(password)) {
        // If correct, redirect to UserProfile.jsp
        response.sendRedirect("UserProfile.jsp");
    } else {
        // If incorrect, redirect to Login.htm
        response.sendRedirect("Login.htm");
    }
%>
