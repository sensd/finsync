<html>
<head>
    <title>Welcome!</title>
</head>
<body>
<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>Product</td>
        <td>URL</td>
        <td>Name</td>
    </tr>
    </thead>
    <tbody>
    <#list products as key, value>
    <tr>
        <td>${key}</td>
        <td>${value.url}</td>
        <td>${value.name}</td>
    </tr>
    </#list>
    </tbody>
</table>
</body>
</html>