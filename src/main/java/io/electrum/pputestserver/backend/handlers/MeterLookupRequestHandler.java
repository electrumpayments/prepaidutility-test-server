package io.electrum.pputestserver.backend.handlers;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.backend.builders.MeterLookupResponseBuilder;
import io.electrum.pputestserver.backend.builders.ResponseBuilder;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.MeterLookupRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class MeterLookupRequestHandler implements RequestHandler<MeterLookupRequest> {

   // TODO inject builders etc.
   public MeterLookupRequestHandler() {
   }

   @Override
   public void handleRequest(
         MeterLookupRequest requestBody,
         String lookupId,
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
      if (!Utils.isUuidConsistent(lookupId, requestBody.getId())) {
         asyncResponse.resume(ErrorDetailFactory.getInconsistentUuidErrorDetail(lookupId, requestBody.getId()));
         return;
      }

      /*
       * Persist in mock DB
       */
      if (!MockServerDb.add(requestBody)) {
         asyncResponse.resume(ErrorDetailFactory.getNotUniqueUuidErrorDetail(requestBody.getId()));
         return;
      }

      ResponseBuilder<MeterLookupRequest> responseBuilder;

      /*
       * Check if this is an error scenario and hand off to error handler if it is
       */
      if (MockResponseTemplates.isErrorScenario(requestBody.getMeter().getMeterId())) {
         new TransactionErrorHandler(req -> ((MeterLookupRequest) req).getMeter()).handleRequest(
               requestBody,
               lookupId,
               securityContext,
               asyncResponse,
               request,
               httpServletRequest,
               httpHeaders,
               uriInfo);
      }
      if (MockResponseTemplates.isDynamicScenario(requestBody.getMeter().getMeterId(), "meterLookup")) {
         /*
          * Get ResponseBuilder
          */
         responseBuilder =
               (ResponseBuilder<MeterLookupRequest>) MockResponseTemplates
                     .getDynamicResponseBuilder(requestBody.getMeter().getMeterId(), "meterLookup");
      } else {

         /*
          * Build Response
          */
         responseBuilder = new MeterLookupResponseBuilder();
      }
      /*
       * Build and send error response if no match is found
       */
      try {
         asyncResponse.resume(responseBuilder.getResponsePayload(requestBody));
      } catch (UnknownMeterException e) {
         new TransactionErrorHandler(req -> ((MeterLookupRequest) req).getMeter()).handleRequest(
               requestBody,
               lookupId,
               securityContext,
               asyncResponse,
               request,
               httpServletRequest,
               httpHeaders,
               uriInfo,
               e);
      }
   }
}
