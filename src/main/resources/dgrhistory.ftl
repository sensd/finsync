<html>
<head>
    <title>Welcome!</title>
</head>
<body>

<h5>Ticker: ${dgrprofile.ticker}</h5>
<h5>DGR 5y: ${dgrprofile.dgr5y}</h5>
<h5>DGR Last: ${dgrprofile.dgrLast}</h5>
<h5>DGR Rating 5y: ${dgrprofile.rating5y}</h5>
<h5>DGR Rating Last: ${dgrprofile.ratingLast}</h5>



<table border="1" class="demoTable" style="height: 54px;">
    <thead>
    <tr>
        <td>pay-date</td>
        <td>Amount</td>
        <td>Prev-amount</td>
        <td>days-between</td>
        <td>DGR %</td>
        <td>Rating</td>
    </tr>
    </thead>
    <tbody>
    <#list dgrprofile.dgrHistory as dgrevent>
    <tr>
        <td>${dgrevent.divPayDate!"N/A"}</td>
        <td>${dgrevent.divAmount}</td>
        <td>${dgrevent.divPrevAmount}</td>
        <td>${dgrevent.daysBetween}</td>
        <td>${dgrevent.growthPercentage}</td>
        <td>${dgrevent.rating} <#if dgrevent.split> Split </#if></td>
    </tr>
    </#list>
    </tbody>
</table>
</body>
</html>
