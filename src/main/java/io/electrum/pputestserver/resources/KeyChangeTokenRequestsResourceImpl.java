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
import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.api.IKeyChangeTokenRequestsResource;
import io.electrum.prepaidutility.api.KeyChangeTokenRequestsResource;
import io.electrum.prepaidutility.model.KeyChangeTokenRequest;
import io.electrum.prepaidutility.model.KeyChangeTokenResponse;
import io.electrum.prepaidutility.model.Meter;

@Path("/prepaidutility/v1/keyChangeTokenRequests")
public class KeyChangeTokenRequestsResourceImpl extends KeyChangeTokenRequestsResource
      implements IKeyChangeTokenRequestsResource {

   static KeyChangeTokenRequestsResourceImpl instance = null;

   @Override
   protected IKeyChangeTokenRequestsResource getResourceImplementation() {
      if (instance == null) {
         instance = new KeyChangeTokenRequestsResourceImpl();
      }
      return instance;
   }

   @Override
   public void createKeyChangeTokenRequest(
         String requestId,
         KeyChangeTokenRequest requestBody,
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
       * Lookup response corresponding to this meter id
       */
      Meter meter = requestBody.getMeter();
      KeyChangeTokenResponse responseBody = MockResponseTemplates.getKctResponse(meter.getMeterId());

      /*
       * Build and send error response if no match is found
       */
      if (responseBody == null) {
         asyncResponse.resume(
               ErrorDetailFactory.getNoTestCaseForMeterId("No key change token for meter id: " + meter.getMeterId()));
         return;
      }

      /*
       * Build and send positive response
       */
      Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
      Utils.logMessageTrace(responseBody);
      asyncResponse.resume(Response.status(Response.Status.ACCEPTED).entity(responseBody).build());
   }
}
