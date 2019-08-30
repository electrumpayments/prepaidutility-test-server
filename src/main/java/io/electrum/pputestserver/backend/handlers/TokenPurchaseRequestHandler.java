package io.electrum.pputestserver.backend.handlers;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.backend.builders.ExampleDetailMessage;
import io.electrum.pputestserver.backend.builders.IResponseBuilder;
import io.electrum.pputestserver.backend.builders.PurchaseResponseBuilder;
import io.electrum.pputestserver.backend.exceptions.IMeterException;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.PurchaseRequest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class TokenPurchaseRequestHandler implements IRequestHandler<PurchaseRequest> {

   // TODO inject builders etc.
   public TokenPurchaseRequestHandler() {
   }

   @Override
   public void handleRequest(
         PurchaseRequest requestBody,
         String purchaseId,
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
       * Check if this is an error scenario and hand off to error handler if it is
       */
      if (MockResponseTemplates.isErrorScenario(requestBody.getMeter().getMeterId())) {
         new TransactionErrorHandler(req -> ((PurchaseRequest) req).getMeter()).handleRequest(
               requestBody,
               purchaseId,
               securityContext,
               asyncResponse,
               request,
               httpServletRequest,
               httpHeaders,
               uriInfo);
      } else {

         /*
          * Lookup response corresponding to this meter id
          */
         Meter meter = requestBody.getMeter();

         /*
          * Simulate timeout for retry scenarios
          */
         if (isRetry(meter.getMeterId())) {
            try {
               Thread.sleep(30000);
               new TransactionErrorHandler(req -> ((PurchaseRequest) req).getMeter()).handleRequest(
                     requestBody,
                     purchaseId,
                     securityContext,
                     asyncResponse,
                     request,
                     httpServletRequest,
                     httpHeaders,
                     uriInfo,
                     new IMeterException() {
                        @Override
                        public Meter getMeter() {
                           return requestBody.getMeter();
                        }

                        @Override
                        public Response buildErrorDetailResponse(
                              String msgId,
                              String originalMsgId,
                              ErrorDetail.RequestType requestType,
                              Meter meter) {
                           ErrorDetail errorDetail = (ErrorDetail) ErrorDetailFactory.getUpstreamTimeout().getEntity();
                           errorDetail.setDetailMessage(new ExampleDetailMessage());
                           errorDetail.setId(msgId);
                           errorDetail.setOriginalId(originalMsgId);
                           errorDetail.setRequestType(requestType);
                           return Response.status(Response.Status.GATEWAY_TIMEOUT).entity(errorDetail).build();
                        }
                     });
               return;
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         /*
          * Check that request amount matches that specified for test case
          */
         if (!Utils.isValidRequestAmount(requestBody, meter.getMeterId())) {
            new TransactionErrorHandler(req -> ((PurchaseRequest) req).getMeter()).handleRequest(
                  requestBody,
                  purchaseId,
                  securityContext,
                  asyncResponse,
                  request,
                  httpServletRequest,
                  httpHeaders,
                  uriInfo,
                  new IMeterException() {
                     @Override
                     public Meter getMeter() {
                        return requestBody.getMeter();
                     }

                     @Override
                     public Response buildErrorDetailResponse(
                           String msgId,
                           String originalMsgId,
                           ErrorDetail.RequestType requestType,
                           Meter meter) {
                        ErrorDetail errorDetail =
                              (ErrorDetail) ErrorDetailFactory.getInvalidRequestAmount().getEntity();
                        errorDetail.setDetailMessage(new ExampleDetailMessage());
                        errorDetail.setId(msgId);
                        errorDetail.setOriginalId(originalMsgId);
                        errorDetail.setRequestType(requestType);
                        return Response.status(Response.Status.GATEWAY_TIMEOUT).entity(errorDetail).build();
                     }
                  });
            return;
         }

         /*
          * Build Response
          */
         IResponseBuilder<PurchaseRequest> purchaseResponseBuilder = new PurchaseResponseBuilder();

         /*
          * Build and send error response if no match is found
          */
         try {
            asyncResponse.resume(purchaseResponseBuilder.getResponsePayload(requestBody));
         } catch (UnknownMeterException e) {
            new TransactionErrorHandler(req -> ((MeterLookupRequest) req).getMeter()).handleRequest(
                  requestBody,
                  purchaseId,
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
