<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>Banki.ru RSS</title>
  <link rel="icon" href="favicon.ico" type="image/x-icon">
  <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<form action="${pageContext.request.contextPath}/banki" method="get">
  Enter banks names (comma-separated):<br/>
  <input type="text" name="names" required/><br/><br/>
  Enter banks IDs (comma-separated, optional):<br/>
  <input type="text" name="ids"/><br/><br/>
  <input type="submit" value="Submit"/>
</form>
</body>
</html>