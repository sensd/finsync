<html>
<head>
    <title>Welcome!</title>
</head>
<body>
<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>Ticker</td>
        <td>January</td>
        <td>February</td>
        <td>March</td>
        <td>April</td>
        <td>May</td>
        <td>June</td>
        <td>July</td>
        <td>August</td>
        <td>September</td>
        <td>October</td>
        <td>November</td>
        <td>December</td>
        <td>Total</td>
    </tr>
    </thead>
    <tbody>

    <#list account.portfolio as key, holding>
    <tr>
        <td>${holding.ticker}</td>
        <#list account.monthlyDivAmount as mdiv>
            <td>
                <#list account.monthlyDivHoldings[mdiv?index] as holdingm>
                    <#if holdingm.ticker == holding.ticker>
                        ${holding.nextDivAmountTotal}
                    </#if>
                </#list>
            </td>
        </#list>
        <td>${holding.annualDivAmount}</td>
    </tr>
    </#list>

    <tr>
        <td>TotalNext</td>
        <#list account.monthlyDivAmountNext as mdiv>
        <td>${mdiv}</td>
        </#list>
        <td></td>
    </tr>
    <tr>
        <td>TotalPrev</td>
        <#list account.monthlyDivAmount as mdiv>
        <td>${mdiv}</td>
    </#list>
    <td></td>
    </tr>
    </tbody>
</table>
</body>
</html>