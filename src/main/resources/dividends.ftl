<html>
<head>
    <title>Welcome!</title>
</head>
<body>


<h4>DGR Profile</h4>
<#list dgrbins as rating, dgrbin>
    <h6>DGR Rating: ${rating} Count ${dgrbin[2]} Original-Cost-Basis: ${dgrbin[0]}  Current Value ${dgrbin[1]} </h6>
</#list>


<h4>Upcoming Dividends </h4>

<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>Date</td>
        <td>Ticker</td>
        <td>Amount</td>
    </tr>
    </thead>
    <tbody>
    <#list upcoming as div>
    <tr>
        <td>${div.nextDivDatePayment}</td>
        <td>${div.ticker}</td>
        <td>${div.nextDivAmountTotal}</td>
    </tr>
    </#list>
    </tbody>
</table>


<h4>Past Dividends </h4>

<#assign count = 0>

<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>#</td>
        <td>Date</td>
        <td>Ticker</td>
        <td>Expected-Amount</td>
        <td>Received-Amount</td>
    </tr>
    </thead>
    <tbody>
    <#list past as div>
    <tr>
        <#assign count += 1>
        <td>${count}</td>
        <td>${div.expectedDate}</td>
        <td>${div.ticker}</td>
        <td>${div.expectedAmount}</td>
        <td>${(div.matchingTransaction.amount)!"NotReceived"}</td>
    </tr>
    </#list>
    </tbody>
</table>

</body>
</html>