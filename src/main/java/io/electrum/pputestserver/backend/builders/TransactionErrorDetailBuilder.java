package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.exceptions.IMeterException;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.vas.model.Transaction;

import javax.ws.rs.core.Response;

public class TransactionErrorDetailBuilder implements IErrorResponseBuilder<Transaction> {

   @Override
   public Response getErrorResponse(Transaction requestBody, Meter meter) {
      // Look for the meter
      ErrorDetail errorDetail =
            MockResponseTemplates.getErrorDetailResponse(meter.getMeterId())
                  .orElseThrow(() -> new UnknownMeterException(meter, Response.Status.BAD_REQUEST));

      errorDetail.setId(requestBody.getId());
      errorDetail.setRequestType(Utils.determineRequestType(requestBody));
      errorDetail.setDetailMessage(new ExampleDetailMessage());

      return Response.status(Utils.getStatusFromErrorType(errorDetail.getErrorType())).entity(errorDetail).build();
   }

   @Override
   public Response getErrorResponseFromException(Transaction requestBody, IMeterException meterException) {
      // Use exception type to figure out response
      return meterException.buildErrorDetailResponse(
            requestBody.getId(),
            null,
            Utils.determineRequestType(requestBody),
            meterException.getMeter());
   }
}
