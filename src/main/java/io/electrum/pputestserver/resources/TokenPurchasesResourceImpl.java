package io.electrum.pputestserver.resources;

import java.util.UUID;

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
import io.electrum.prepaidutility.api.ITokenPurchasesResource;
import io.electrum.prepaidutility.api.TokenPurchasesResource;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.TenderAdvice;

@Path("/prepaidutility/v1/tokenPurchases")
public class TokenPurchasesResourceImpl extends TokenPurchasesResource implements ITokenPurchasesResource {

   static TokenPurchasesResourceImpl instance = null;

   @Override
   protected ITokenPurchasesResource getResourceImplementetion() {
      if (instance == null) {
         instance = new TokenPurchasesResourceImpl();
      }
      return instance;
   }

   @Override
   public void confirmTokenPurchase(
         String purchaseId,
         String confirmationId,
         TenderAdvice requestBody,
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
      PurchaseResponse responseBody = MockResponseTemplates.getPurchaseResponse(meter.getMeterId());
      
      //TODO: simulate timeout for retry scenario

      /*
       * Build and send error response if no match is found
       */
      if (responseBody == null) {
         asyncResponse.resume(ErrorDetailFactory.getUnknownMeterIdErrorDetail(meter.getMeterId()));
         return;
      }

      /*
       * Build and send positive response
       */
      Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
      Utils.logMessageTrace(responseBody);
      asyncResponse.resume(Response.status(Response.Status.ACCEPTED).entity(responseBody).build());
   }

   @Override
   public void retryPurchaseRequest(
         String purchaseId,
         String retryId,
         PurchaseRequest requestBody,
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
       * Lookup original request, if not found, create new
       */
      PurchaseRequest originalRequest = MockServerDb.getPurchaseRequest(UUID.fromString(purchaseId));
      if (originalRequest == null) {
         if (!MockServerDb.add(requestBody)) {
            asyncResponse.resume(ErrorDetailFactory.getNotUniqueUuidErrorDetail(UUID.fromString(purchaseId)));
            return;
         }
      }

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
       * Build and send positive response
       */
      Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
      Utils.logMessageTrace(responseBody);
      asyncResponse.resume(Response.status(Response.Status.ACCEPTED).entity(responseBody).build());
   }

   @Override
   public void reverseTokenPurchase(
         String purchaseId,
         String reversalId,
         BasicReversal requestBody,
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

}
