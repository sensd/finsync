<html>
<head>
    <title>Welcome!</title>
</head>
<body>
<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>Month</td>
        <td>Dividend</td>
        <td>Details</td>
    </tr>
    </thead>
    <tbody>
    <#list account.monthlyDivAmount as mdiv>
    <tr>
        <td>${mdiv?index}</td>
        <td>${mdiv}</td>
        <td>
            <#list account.monthlyDivHoldings[mdiv?index] as holding>
            <tr>
                <td>${holding.ticker}</td>
                <td>${holding.nextDivAmountTotal}</td>
            </tr>
            </#list>

        </td>
    </tr>
    </#list>
    </tbody>
</table>
</body>
</html>