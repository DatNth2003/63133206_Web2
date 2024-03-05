<%@ page pageEncoding="UTF-8"%>
 <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="<c:url value="/assets/css/styles.css" />" rel="stylesheet">
	<!-- no jstl, use 
	<link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet" >	
	 -->
<script src="<c:url value="/assets/js/test.js" />"></script>
<script src="${pageContext.request.contextPath}/assets/js/jquery.1.10.2.min.js" />"></script>
<title>Insert title here</title>
</head>
<body>
	<h1>Chào  mừng MVC</h1>
	
	${msg}
</body>
</html>