<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>RSS full description</title>
  <link rel="icon" href="favicon.ico" type="image/x-icon">
  <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<form action="${pageContext.request.contextPath}/championat" method="get">
  Maximum number of feed entries:<br/>
  <select name="limit">
    <option>5</option>
    <option>10</option>
    <option selected>25</option>
    <option>50</option>
  </select>
  <br/><br/>
  <input type="checkbox" name="breaking_only"> Breaking news and articles only
  <br/><br/>
  <input type="submit" value="Submit"/><br/><br/>
</form>
</body>
</html>