package io.electrum.pputestserver.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.api.ITokenPurchasesResource;
import io.electrum.prepaidutility.api.TokenPurchasesResource;
import io.electrum.prepaidutility.model.PurchaseRequest;
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
      // TODO
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
      Utils.validateRequest(requestBody, asyncResponse);

      Utils.logMessageTrace(requestBody);

      // TODO
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
      // TODO
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
      // TODO
   }

}
