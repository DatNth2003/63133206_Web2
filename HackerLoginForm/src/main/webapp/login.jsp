<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*" %>
<form action="login.jsp" method="post">

<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    if (username.equals("ABC") && password.equals("MNK")) {
        response.sendRedirect("UserProfile.html");
    } else {
        response.sendRedirect("login.html");
    }
%>
</form>