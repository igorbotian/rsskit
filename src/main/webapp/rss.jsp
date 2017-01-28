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
    <script type="text/javascript">
        function onRssFormSubmit() {
            var rssForm = document.getElementById('rss_settings_form');
            var allInputs = rssForm.getElementsByTagName('input');
            var input, i;

            for(i = 0; input = allInputs[i]; i++) {
                if(input.getAttribute('name') && (!input.value || 0 === input.value.length)) {
                    input.setAttribute('name', '');
                }
            }

            var allSelects = rssForm.getElementsByTagName('select');
            var select;

            for(i = 0; select = allSelects[i]; i++) {
                if(!select.options[select.selectedIndex].value || 0 === select.options[select.selectedIndex].value.length) {
                    // in order not to send default value of the select to the servlet as URL parameter
                    select.disabled = true;
                }
            }
        };
    </script>
</head>
<body>
<form action="${pageContext.request.contextPath}/rss" method="get" id="rss_settings_form" onSubmit="onRssFormSubmit()">
    Enter an RSS feed URL below:<br/>
    <input type="text" name="url" required/><br/><br/>
    Categories (comma-separated):<br/>
    <input name="categories" type="text"/><br/><br/>
    Authors (comma-separated):<br/>
    <input name="authors" type="text"/><br/><br/>
    Maximum number of feed entries:<br/>
    <select name="size">
        <option value="" selected>5</option>
        <option value="10">10</option>
        <option value="25">25</option>
        <option value="50">50</option>
    </select>
    <br/><br/>
    <!--Reading service:<br/>
    <input type="radio" name="service" value="mercury" title="Mercury" checked/>&nbsp;Mercury
    <input type="radio" name="service" value="instapaper" title="Instapaper"/>&nbsp;Instapaper<br/>
    <br/>-->
    Mobile version host:<br/>
    <input name="mobile_version_host" type="text"/><br/><br/>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>