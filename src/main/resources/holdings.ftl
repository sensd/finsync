<html>
<head>
    <title>Welcome!</title>


    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>

    <!-- choose a theme file -->
    <link rel="stylesheet" href="../../static/css/theme.blue.css">
    <!-- load jQuery and tablesorter scripts -->
    <script type="text/javascript" src="../../static/js/jquery.tablesorter.min.js"></script>

    <script id="js">$(function() {
    // initial sort set using sortList option
	$(".holdingstable").tablesorter({
		theme : 'blue',
		// sort on the first column and second column in ascending order
		sortList: [[1,0]]
	});
    });</script>

</head>
<body>


<h5>Portfolio Cost Basis: ${account.portfolioCostBasis}</h5>
<h5>Portfolio Cash: ${account.cashValue}</h5>
<h5>Portfolio Value: ${account.portfolioValue}</h5>
<h5>Portfolio Annual Dividend: ${account.totalAnnualDivAmount}</h5>

<#assign totaloriginalcostbasis = 0>
<#assign totalcostbasis = 0>
<#assign totalvalue = 0>
<#assign totalvaluechange = 0>
<#assign totalpercentchange = 0>
<#assign totalyieldoncost = 0>
<#assign totalyieldonoriginalcost = 0>
<#assign totalannualdiv = 0>
<#assign totaldivreceived = 0>
<#assign totalgloriginalcostbasis = 0>
<#assign totalglcostbasis = 0>
<#assign totalreturnoriginalcostbasis = 0>
<#assign totalreturncostbasis = 0>

<#assign count = 0>

<table border = "1" class="holdingstable tablesorter-blue">
    <thead>
    <tr>
        <td>#</td>
        <td>ticker</td>
        <td>shares</td>
        <td>price</td>
        <td>avg-purchase-price</td>
        <td>original-cost-basis</td>
        <td>cost-basis</td>
        <td>value</td>
        <td>value-change</td>
        <td>%-change</td>
        <td>current-yield</td>
        <td>yield-on-cost</td>
        <td>yield-on <b/> original <b/> cost</td>
        <td>next-div-payment-date</td>
        <td>next-div-amount-share</td>
        <td>next-div-amount-total</td>
        <td>last-div-payment-date</td>
        <td>last-div-amount-share</td>
        <td>last-div-amount-total</td>
        <td>annual-div-total</td>
        <td>payout-ratio</td>
        <td>div-frequency</td>
        <td>div-reinvestment</td>
        <td>total-div-received</td>
        <td>gl-original-cost-basis</td>
        <td>gl-cost-basis</td>
        <td>total-return-original-cost-basis</td>
        <td>total-return-cost-basis</td>
        <td>first-buy-date</td>
        <td>DGR-5y</td>
        <td>DGR-Last</td>
        <td>DGR-Rating5y</td>
        <td>DGR-RatingLast</td>
        <td>ticker</td>
    </tr>
    </thead>
    <tbody>
    <#list account.portfolio as key, holding>
    <tr>
        <#assign count += 1>
        <td>${count}</td>
        <td>${holding.ticker}
            <a href="/api/finsync/transactions?ticker=${holding.ticker}">transactions</a>
            <a href="/api/finsync/divtransactions?ticker=${holding.ticker}">dividends</a>
        </td>
        <td>${holding.shares}</td>
        <td>${holding.price}</td>
        <td>${holding.avgPurchasePrice}</td>
        <td>${holding.originalCostBasis}</td>
        <td>${holding.costBasis}</td>
        <td>${holding.value}</td>
        <td>${holding.valueChange}</td>
        <td>${holding.percentChange}%</td>
        <td>${holding.currentYield}%</td>
        <td>${holding.yieldOnCostBasis}%</td>
        <td>${holding.yieldOnOriginalCostBasis}%</td>
        <td>${holding.nextDivDatePayment!"N/A"}</td>
        <td>${holding.nextDivAmountPerShare}</td>
        <td>${holding.nextDivAmountTotal}</td>
        <td>${holding.lastDivDatePayment!"N/A"}</td>
        <td>${holding.lastDivAmountPerShare}</td>
        <td>${holding.lastDivAmountTotal}</td>
        <td>${holding.annualDivAmount}</td>
        <td>${holding.divPayoutRatio}%</td>
        <td>${holding.divFrequency}</td>
        <td>${holding.reinvestment!"N/A"}</td>
        <td>${holding.totalDivReceived}</td>
        <td>${holding.gainlossOnOriginalCostBasis}</td>
        <td>${holding.gainlossOnCostBasis}</td>
        <td>${holding.totalReturnOnOriginalCostBasis}%</td>
        <td>${holding.totalReturnOnCostBasis}%</td>
        <td>${holding.firstBuyDate!"N/A"}</td>
        <td>${(holding.divDGRProfile.dgr5y)!"N/A"}% </td>
        <td>${(holding.divDGRProfile.dgrLast)!"N/A"}% </td>
        <td>${(holding.divDGRProfile.rating5y)!"N/A"} </td>
        <td>${(holding.divDGRProfile.ratingLast)!"N/A"}
            <a href="/api/finsync/dgrhistory?ticker=${holding.ticker}">DGR</a>
        </td>
        <td>${holding.ticker}</td>

        <#assign totaloriginalcostbasis += holding.originalCostBasis>
        <#assign totalcostbasis += holding.costBasis>
        <#assign totalvalue += holding.value>
        <#assign totalvaluechange += holding.valueChange>
        <#assign totalannualdiv += holding.annualDivAmount>
        <#assign totaldivreceived += holding.totalDivReceived>
        <#assign totalgloriginalcostbasis += holding.gainlossOnOriginalCostBasis>
        <#assign totalglcostbasis += holding.gainlossOnCostBasis>
    </tr>
    </#list>
    <#if totalvaluechange lt 0>
        <#assign totalpercentchange = (totalvaluechange * 100)/(totalvalue + totalvaluechange) >
    <#else>
        <#assign totalpercentchange = (totalvaluechange * 100)/(totalvalue - totalvaluechange) >
    </#if>
    <#assign totalpercentchange = (totalvaluechange * 100)/totalcostbasis>
    <#assign totalyieldoncurrentvalue = (totalannualdiv * 100)/totalvalue>
    <#assign totalyieldonoriginalcost = (totalannualdiv * 100)/totaloriginalcostbasis>
    <#assign totalyieldoncost = (totalannualdiv * 100)/totalcostbasis>
    <#assign totalreturnoriginalcostbasis = (totalgloriginalcostbasis * 100)/totaloriginalcostbasis>
    <#assign totalreturncostbasis = (totalglcostbasis * 100)/totalcostbasis>
    <tr>
        <td>Total:</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>${totaloriginalcostbasis}</td>
        <td>${totalcostbasis}</td>
        <td>${totalvalue}</td>
        <td>${totalvaluechange}</td>
        <td>${totalpercentchange}%</td>
        <td>${totalyieldoncurrentvalue}%</td>
        <td>${totalyieldoncost}%</td>
        <td>${totalyieldonoriginalcost}%</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>${totalannualdiv}</td>
        <td></td>
        <td></td>
        <td></td>
        <td>${totaldivreceived}</td>
        <td>${totalgloriginalcostbasis}</td>
        <td>${totalglcostbasis}</td>
        <td>${totalreturnoriginalcostbasis}%</td>
        <td>${totalreturncostbasis}%</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    </tbody>
</table>

</body>
</html>