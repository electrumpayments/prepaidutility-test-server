package io.electrum.pputestserver.resources;

import java.io.IOException;

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

@Path("/prepaidutility/v2/keyChangeTokenRequests")
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
       * Log incoming message trace
       */
      Utils.logMessageTrace(requestBody);
      
      /*
       * Validate request
       */
      if (!Utils.validateRequest(requestBody, asyncResponse)) {
         return;
      }
      if (!Utils.isUuidConsistent(requestId, requestBody.getId())) {
         asyncResponse.resume(ErrorDetailFactory.getInconsistentUuidErrorDetail(requestId, requestBody.getId()));
         return;
      }

      /*
       * Persist in mock DB
       */
      if (!MockServerDb.add(requestBody)) {
         asyncResponse.resume(ErrorDetailFactory.getNotUniqueUuidErrorDetail(requestBody.getId()));
         return;
      }
      
      /*
       * Build and send error response if meter ID is unknown
       */
      Meter meter = requestBody.getMeter();
      if (!MockResponseTemplates.meterExists(meter.getMeterId())) {
         asyncResponse.resume(ErrorDetailFactory.getUnknownMeterIdErrorDetail(meter.getMeterId()));
         return;
      }

      /*
       * Lookup mock response
       */
      try {
         KeyChangeTokenResponse responseBody = MockResponseTemplates.getKctResponse();
         Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
         Utils.logMessageTrace(responseBody);
         asyncResponse.resume(Response.status(Response.Status.CREATED).entity(responseBody).build());
      } catch (IOException e) {
         asyncResponse.resume(ErrorDetailFactory.getInternalServerError("Error reading data"));
      }
   }
}
