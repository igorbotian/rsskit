<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Readability RSS</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<form action="${pageContext.request.contextPath}/proxy" method="get">
    Enter an URL to your <a href="http://help.readability.com/customer/portal/articles/254211-are-there-rss-feeds-for-my-reading-list-">Readability RSS</a> feed below:<br/>
    <input type="text" name="url" required/>&nbsp;&nbsp;<input type="submit" value="Submit"/>
</form>
</body>
</html>