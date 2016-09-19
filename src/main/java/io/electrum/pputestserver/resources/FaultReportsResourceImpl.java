package io.electrum.pputestserver.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.api.FaultReportsResource;
import io.electrum.prepaidutility.api.IFaultReportsResource;
import io.electrum.prepaidutility.model.FaultReportRequest;
import io.electrum.prepaidutility.model.FaultReportResponse;

@Path("/prepaidutility/v1/faultReports")
public class FaultReportsResourceImpl extends FaultReportsResource implements IFaultReportsResource {

   static FaultReportsResourceImpl instance = null;

   @Override
   protected IFaultReportsResource getResourceImplementation() {
      if (instance == null) {
         instance = new FaultReportsResourceImpl();
      }
      return instance;
   }

   @Override
   public void createFaultReport(
         String requestId,
         FaultReportRequest requestBody,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo) {
      /*
       * Validate request
       */
      if (!Utils.validateRequest(requestBody, asyncResponse)) {
         return;
      }

      Utils.logMessageTrace(requestBody);

      /*
       * Persist in mock DB
       */
      if (!MockServerDb.add(requestBody)) {
         asyncResponse.resume(ErrorDetailFactory.getNotUniqueUuidErrorDetail(requestBody.getId()));
         return;
      }

      /*
       * Build and send positive response
       */
      FaultReportResponse responseBody = new FaultReportResponse();
      Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
      responseBody.setReference(generateReference(requestBody));
      responseBody.setDescription("Fault description to be printed on receipt");
      Utils.logMessageTrace(responseBody);
      asyncResponse.resume(Response.status(Response.Status.CREATED).entity(responseBody).build());
   }

   private static String generateReference(FaultReportRequest requestBody) {
      return requestBody.getTime().toString() + requestBody.getOriginator().getTerminalId();
   }
}
