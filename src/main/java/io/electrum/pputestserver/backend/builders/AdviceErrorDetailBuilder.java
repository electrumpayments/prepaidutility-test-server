package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.exceptions.IMeterException;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.vas.model.BasicAdvice;

import javax.ws.rs.core.Response;

public class AdviceErrorDetailBuilder implements IErrorResponseBuilder<BasicAdvice> {

   @Override
   public Response getErrorResponse(BasicAdvice requestBody, Meter meter) {
      // Look for the meter
      ErrorDetail errorDetail =
            MockResponseTemplates.getErrorDetailResponse(meter.getMeterId())
                  .orElseThrow(() -> new UnknownMeterException(meter, Response.Status.BAD_REQUEST));

      return Response.status(Utils.getStatusFromErrorType(errorDetail.getErrorType())).entity(errorDetail).build();
   }

   @Override
   public Response getErrorResponseFromException(BasicAdvice requestBody, IMeterException meterException) {
      // Use exception type to figure out response
      return meterException.buildErrorDetailResponse(
            requestBody.getId(),
            requestBody.getRequestId(),
            Utils.determineRequestType(requestBody),
            meterException.getMeter());
   }
}
