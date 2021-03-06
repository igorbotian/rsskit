<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>rsskit</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<span class="header">RSS feeds</span>
<ol>
    <li><a href="${pageContext.request.contextPath}/facebook">Facebook news feed</a></li>
    <li><a href="${pageContext.request.contextPath}/facebook?notifications=true">Facebook notifications</a></li>
    <li><a href="${pageContext.request.contextPath}/instagram">Instagram self feed</a></li>
    <li><a href="${pageContext.request.contextPath}/twitter">Twitter home timeline</a></li>
    <li><a href="${pageContext.request.contextPath}/vk.jsp">VK home feed</a></li>
</ol>
<span class="header">Tools</span>
<ol>
    <li><a href="${pageContext.request.contextPath}/proxy.jsp">Proxy</a></li>
    <li><a href="${pageContext.request.contextPath}/instapaper.jsp">Reading view (Instapaper)</a></li>
    <li><a href="${pageContext.request.contextPath}/mercury.jsp">Reading view (Mercury)</a></li>
    <li><a href="${pageContext.request.contextPath}/rss.jsp">RSS full description</a></li>
</ol>
<span class="header">Custom RSS feeds</span><br/>
<ol>
    <li><a href="${pageContext.request.contextPath}/reading_list">Reading list (based on links shared via Twitter)</a></li>
    <li><a href="${pageContext.request.contextPath}/championat.jsp">championat.com (футбол; главные новости)</a></li>
    <li><a href="${pageContext.request.contextPath}/asot">A state of trance (CueNation.com)</a></li>
    <li><a href="${pageContext.request.contextPath}/banki.jsp">banki.ru (certain banks)</a></li>
    <li><a href="${pageContext.request.contextPath}/zenit">FC Zenit calendar (WebCal)</a></li>
</ol>
</body>
</html>