package org.klose.payment.server.rest;

import org.klose.payment.server.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("paymentservice/")
@Component
public class PaymentServiceResources {
	@Autowired
	TransactionDao transDao;
	
	@GET
	@Path("/return/{transId}")
	public Response returnProxy(@PathParam("transId") Long transId,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws URISyntaxException {		
		
		String uri = transDao.findReturnURLById(transId);
		return Response.temporaryRedirect(new URI(uri)).build();
	}	
}
