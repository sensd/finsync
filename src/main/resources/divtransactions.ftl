<html>
<head>
    <title>Welcome!</title>
</head>
<body>
<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>ticker</td>
        <td>expected-date</td>
        <td>expected-amount</td>

        <td>matching-date</td>
        <td>matching-type</td>
        <td>matching-shares</td>
        <td>matching-price</td>
        <td>matching-amount</td>
    </tr>
    </thead>
    <tbody>
    <#list divtransactions as divtransaction>
    <tr>
        <td>${divtransaction.ticker}</td>
        <td>${divtransaction.expectedDate!"N/A"}</td>
        <td>${divtransaction.expectedAmount}</td>
        <td>${(divtransaction.matchingTransaction.date)!"N/A"}</td>
        <td>${(divtransaction.matchingTransaction.type)!"N/A"}</td>
        <td>${(divtransaction.matchingTransaction.shares)!"N/A"}</td>
        <td>${(divtransaction.matchingTransaction.price)!"N/A"}</td>
        <td>${(divtransaction.matchingTransaction.amount)!"N/A"}</td>
    </tr>
    </#list>
    </tbody>
</table>
</body>
</html>