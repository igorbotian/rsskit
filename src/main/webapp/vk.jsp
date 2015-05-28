<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>VK news feed RSS</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
1. Open this <a href="${pageContext.request.contextPath}/vk" target="_blank">link</a> (it will be opened in a new window/tab).<br/><br/>
2. Copy a <span style="font-weight: bold">code</span> parameter value from the address bar: <br/>
<img src="${pageContext.request.contextPath}/images/vk_code.png" alt="VK code parameter"/><br/><br/>
3. Paste the copied value into the text field below:<br/>
<form action="${pageContext.request.contextPath}/vk">
    <input type="text" name="code" required/>&nbsp;&nbsp;<input type="submit" value="Submit"/>
</form>
</body>
</html>