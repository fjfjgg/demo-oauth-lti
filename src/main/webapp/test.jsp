<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test</title>
</head>
<body>
<%
session.setAttribute("user", "test");
session.setAttribute("launch_id", "test de chat");
session.setAttribute("role", "profesor");
%>
</body>
</html>