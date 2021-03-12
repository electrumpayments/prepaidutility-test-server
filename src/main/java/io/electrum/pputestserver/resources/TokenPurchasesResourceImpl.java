package io.electrum.pputestserver.resources;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.backend.handlers.TokenPurchaseRequestHandler;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.api.ITokenPurchasesResource;
import io.electrum.prepaidutility.api.TokenPurchasesResource;
import io.electrum.prepaidutility.model.ConfirmationAdvice;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.prepaidutility.model.ReversalAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/prepaidutility/v3")
public class TokenPurchasesResourceImpl extends TokenPurchasesResource implements ITokenPurchasesResource {

   static TokenPurchasesResourceImpl instance = null;
   private static Logger logger = LoggerFactory.getLogger(TokenPurchasesResourceImpl.class);

   @Override
   protected ITokenPurchasesResource getResourceImplementation() {
      if (instance == null) {
         instance = new TokenPurchasesResourceImpl();
      }
      return instance;
   }

   @Override
   public void confirmTokenPurchase(
         String purchaseId,
         String confirmationId,
         ConfirmationAdvice requestBody,
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
      if (!Utils.isUuidConsistent(confirmationId, requestBody.getId())) {
         asyncResponse.resume(ErrorDetailFactory.getInconsistentUuidErrorDetail(confirmationId, requestBody.getId()));
         return;
      }

      /*
       * Lookup original request
       */
      PurchaseRequest originalRequest = MockServerDb.getPurchaseRequest(requestBody.getRequestId());
      if (originalRequest == null) {
         asyncResponse.resume(ErrorDetailFactory.getOriginalRequestNotFound(requestBody.getRequestId()));
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
       * Acknowledge receipt
       */
      asyncResponse.resume(Response.status(Response.Status.ACCEPTED).build());
   }

   @Override
   public void createTokenPurchaseRequest(
         String purchaseId,
         PurchaseRequest requestBody,
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
      new TokenPurchaseRequestHandler().handleRequest(
            requestBody,
            purchaseId,
            securityContext,
            asyncResponse,
            request,
            httpServletRequest,
            httpHeaders,
            uriInfo);
   }

   @Override
   public void retryPurchaseRequest(
         String purchaseId,
         PurchaseRequest requestBody,
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

      /*
       * Add original request if not already present
       */
      MockServerDb.add(requestBody);

      /*
       * Lookup response corresponding to this meter id
       */
      Meter meter = requestBody.getMeter();
      PurchaseResponse responseBody = MockResponseTemplates.getPurchaseResponse(meter.getMeterId());

      /*
       * Build and send error response if no match is found
       */
      if (responseBody == null) {
         asyncResponse.resume(ErrorDetailFactory.getUnknownMeterIdErrorDetail(meter.getMeterId()));
         return;
      }

      /*
       * Check that request amount matches that specified for test case
       */
      if (!checkRequestAmount(requestBody, meter.getMeterId())) {
         asyncResponse.resume(ErrorDetailFactory.getInvalidRequestAmount());
         return;
      }

      /*
       * Build and send positive response
       */
      Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
      Utils.logMessageTrace(responseBody);
      asyncResponse.resume(Response.status(Response.Status.CREATED).entity(responseBody).build());
   }

   @Override
   public void reverseTokenPurchase(
         String purchaseId,
         String reversalId,
         ReversalAdvice requestBody,
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
      if (!Utils.isUuidConsistent(reversalId, requestBody.getId())) {
         asyncResponse.resume(ErrorDetailFactory.getInconsistentUuidErrorDetail(reversalId, requestBody.getId()));
         return;
      }

      /*
       * Lookup original request
       */
      PurchaseRequest originalRequest = MockServerDb.getPurchaseRequest(requestBody.getRequestId());
      if (originalRequest == null) {
         asyncResponse.resume(ErrorDetailFactory.getOriginalRequestNotFound(requestBody.getRequestId()));
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
       * Acknowledge receipt
       */
      asyncResponse.resume(Response.status(Response.Status.ACCEPTED).build());
   }

   /**
    * Check that the request amount falls within the limits set for that meter.
    * 
    * @param requestBody
    * @param meterId
    * @return boolean
    */
   private static boolean checkRequestAmount(PurchaseRequest requestBody, String meterId) {
      return Utils.isValidRequestAmount(requestBody, meterId);
   }
}
