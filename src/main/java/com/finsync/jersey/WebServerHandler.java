package com.finsync.jersey;
import com.finsync.Account;
import com.finsync.FinSync;
import com.finsync.Greeting;
import com.finsync.controller.FrmContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/finsync")
public class WebServerHandler {

	@Context
	Request request;

    private final static Logger LOG = LoggerFactory.getLogger(WebServerHandler.class);

    /*
    @GET
    @Path("/{param}")
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting hello(@PathParam("param") String name) {
        return new Greeting(name);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String helloUsingJson(Greeting greeting) {
        return greeting.getMessage() + "\n";
    }
    */
    @Path("/a")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String helloa() {
        return "test hello a ";
    }

    @Path("/b")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hellob() {
        return "test hello b ";
    }


    @Path("/refresh")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String refresh() {
        try {

            //FinSync.customer.refresh("ally", "4ZW91047");
            return "{\"status\": \"OK\"}";
        } catch (Exception e) {
            LOG.error("exception: ", e);
            return "{\"status\": \"FAILURE\"}";
        }
    }


	@Path("/frmtest")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response frmtest(@Context HttpServletResponse httpServletResponse) {
		String resp = FrmContainer.processFrmex();
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

	@Path("/frmtest1")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response frmtest1(@Context HttpServletResponse httpServletResponse) {
		String resp = FrmContainer.processFrmex1();
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

	@Path("/holdings")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response holdings(@Context HttpServletResponse httpServletResponse) {
		String resp = FrmContainer.processHoldings();
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

	@Path("/monthly")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response monthly(@Context HttpServletResponse httpServletResponse) {
		String resp = FrmContainer.processMonthly();
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

	@Path("/dividends")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response upcomingDividend(@Context HttpServletRequest httpServletRequest,
	                                 @Context HttpServletResponse httpServletResponse) {
		String resp = FrmContainer.processDividends();
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

	@Path("/transactions")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response transactions(@Context HttpServletRequest httpServletRequest,
	                              @Context HttpServletResponse httpServletResponse) {
		String ticker = "";
		String queryString=httpServletRequest.getQueryString();
		String[] params = queryString.split("=");

		if (params[0].equalsIgnoreCase("ticker")) {
			ticker = params[1];
		}
		if (ticker.length() == 0) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		String resp = FrmContainer.processTransactions(ticker);
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

	@Path("/divtransactions")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response divTransactions(@Context HttpServletRequest httpServletRequest,
	                             @Context HttpServletResponse httpServletResponse) {
		String ticker = "";
		String queryString=httpServletRequest.getQueryString();
		String[] params = queryString.split("=");

		if (params[0].equalsIgnoreCase("ticker")) {
			ticker = params[1];
		}
		if (ticker.length() == 0) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		String resp = FrmContainer.processDivTransactions(ticker);
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}


	@Path("/dgrhistory")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response dgrhistory(@Context HttpServletRequest httpServletRequest,
	                                @Context HttpServletResponse httpServletResponse) {
		String ticker = "";
		String queryString=httpServletRequest.getQueryString();
		String[] params = queryString.split("=");

		if (params[0].equalsIgnoreCase("ticker")) {
			ticker = params[1];
		}
		if (ticker.length() == 0) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		String resp = FrmContainer.processDGRHistory(ticker);
		Response response = Response.status(Response.Status.OK).entity(resp).build();
		return response;
	}

}
