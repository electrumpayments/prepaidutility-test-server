package io.electrum.pputestserver.backend.handlers;

import io.electrum.pputestserver.backend.builders.AdviceErrorDetailBuilder;
import io.electrum.pputestserver.backend.builders.ErrorResponseBuilder;
import io.electrum.pputestserver.backend.exceptions.IMeterException;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.vas.model.BasicAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class AdviceErrorHandler extends BaseErrorRequestHandler<BasicAdvice> {

   public AdviceErrorHandler(MeterSupplier<BasicAdvice> meterSupplier) {
      super(meterSupplier);
   }

   @Override
   public void handleRequest(
         BasicAdvice requestBody,
         String lookupId,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo)
         throws UnknownMeterException {
      ErrorResponseBuilder<BasicAdvice> errorResponseBuilder = new AdviceErrorDetailBuilder();
      asyncResponse.resume(errorResponseBuilder.getErrorResponse(requestBody, getMeter(requestBody)));
   }

   @Override
   public void handleRequest(
         BasicAdvice requestBody,
         String lookupId,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo,
         IMeterException exception) {
      ErrorResponseBuilder<BasicAdvice> errorResponseBuilder = new AdviceErrorDetailBuilder();
      asyncResponse.resume(errorResponseBuilder.getErrorResponseFromException(requestBody, exception));
   }
}
