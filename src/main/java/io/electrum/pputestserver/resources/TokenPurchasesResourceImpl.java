package io.electrum.pputestserver.resources;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.api.ITokenPurchasesResource;
import io.electrum.prepaidutility.api.TokenPurchasesResource;
import io.electrum.prepaidutility.model.*;

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

      /*
       * Validate request
       */
      if (!Utils.validateRequest(requestBody, asyncResponse)) {
         return;
      }
      if (!Utils.isUuidConsistent(purchaseId, requestBody.getId())) {
         asyncResponse.resume(ErrorDetailFactory.getInconsistentUuidErrorDetail(purchaseId, requestBody.getId()));
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
       * Lookup response corresponding to this meter id
       */
      Meter meter = requestBody.getMeter();
      PurchaseResponse responseBody = MockResponseTemplates.getPurchaseResponse(meter.getMeterId());

      /*
       * Simulate timeout for retry scenarios
       */
      if (isRetry(meter.getMeterId())) {
         try {
            Thread.sleep(10000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         asyncResponse.resume(ErrorDetailFactory.getUpstreamTimeout());
         return;
      }

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
      if (MockResponseTemplates.getMeterLookupResponse(meterId) == null) {
         logger.error("Failed to get meter info for this test case (meter ID: {})", meterId);
         return false;
      }

      if (MockResponseTemplates.getMeterLookupResponse(meterId).getMinAmount() == null) {
         logger.error("Failed to get minimum request amount for this test case (meter ID: {})", meterId);
         return false;
      }

      if (MockResponseTemplates.getMeterLookupResponse(meterId).getMaxAmount().getAmount() == null) {
         logger.error("Failed to get maximum request amount for this test case (meter ID: {})", meterId);
         return false;
      }

      Long minAmount = MockResponseTemplates.getMeterLookupResponse(meterId).getMinAmount().getAmount();
      Long maxAmount = MockResponseTemplates.getMeterLookupResponse(meterId).getMaxAmount().getAmount();
      Long requestAmount = requestBody.getPurchaseAmount().getAmount();

      return requestAmount.compareTo(minAmount) >= 0 && requestAmount.compareTo(maxAmount) <= 0;
   }

   /**
    * Checks meter number against list of meters configures as retry scenarios
    * 
    * @param meterId
    * @return boolean
    */
   private static boolean isRetry(String meterId) {
      // TODO: get list from config
      List<String> retryMeterIds = new ArrayList<>();
      retryMeterIds.add("TST011");

      return retryMeterIds.contains(meterId);
   }
}
