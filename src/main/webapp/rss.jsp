<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>rsskit proxy</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<form action="${pageContext.request.contextPath}/rss" method="get">
    Enter an RSS feed URL below:<br/>
    <input type="text" name="url" required/>&nbsp;&nbsp;<input type="submit" value="Submit"/><br/><br/>
    Categories (comma-separated):<br/>
    <input name="categories" type="text"/><br/><br/>
    Maximum number of feed entries:<br/>
    <select name="size">
        <option>5</option>
        <option selected>10</option>
        <option>25</option>
        <option>50</option>
    </select>
    <br/><br/>
    Reading service:<br/>
    <input type="radio" name="service" value="instapaper" checked title="Instapaper"/>&nbsp;Instapaper<br/>
    <input type="radio" name="service" value="readability" title="Readability"/>&nbsp;Readability
    <br/><br/>
    Mobile version host:<br/>
    <input name="mobile_version_host" type="text"/>
</form>
</body>
</html>