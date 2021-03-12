package io.electrum.pputestserver.backend.handlers;

import io.electrum.pputestserver.backend.builders.ErrorResponseBuilder;
import io.electrum.pputestserver.backend.builders.TransactionErrorDetailBuilder;
import io.electrum.pputestserver.backend.exceptions.IMeterException;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.vas.model.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class TransactionErrorHandler extends BaseErrorRequestHandler<Transaction> {

   public TransactionErrorHandler(MeterSupplier<Transaction> meterSupplier) {
      super(meterSupplier);
   }

   @Override
   public void handleRequest(
         Transaction requestBody,
         String lookupId,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo)
         throws UnknownMeterException {
      ErrorResponseBuilder<Transaction> errorResponseBuilder = new TransactionErrorDetailBuilder();
      asyncResponse.resume(errorResponseBuilder.getErrorResponse(requestBody, getMeter(requestBody)));
   }

   @Override
   public void handleRequest(
         Transaction requestBody,
         String lookupId,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo,
         IMeterException exception) {
      ErrorResponseBuilder<Transaction> errorResponseBuilder = new TransactionErrorDetailBuilder();
      asyncResponse.resume(errorResponseBuilder.getErrorResponseFromException(requestBody, exception));
   }
}
