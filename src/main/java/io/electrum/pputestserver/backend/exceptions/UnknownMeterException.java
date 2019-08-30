package io.electrum.pputestserver.backend.exceptions;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.Meter;

import javax.ws.rs.core.Response;

public class UnknownMeterException extends RuntimeException implements IMeterException {

   private final Meter meter;
   private final Response.Status associatedHttpStatus;

   public UnknownMeterException(Meter meter, Response.Status associatedHttpStatus) {
      this.meter = meter;
      this.associatedHttpStatus = associatedHttpStatus;
   }

   public Meter getMeter() {
      return meter;
   }

   @Override
   public Response buildErrorDetailResponse(
         String msgId,
         String originalMsgId,
         ErrorDetail.RequestType requestType,
         Meter meter) {
      Response response = ErrorDetailFactory.getUnknownMeterIdErrorDetail(meter.getMeterId(), associatedHttpStatus);
      ErrorDetail errorDetail = (ErrorDetail) response.getEntity();
      errorDetail.id(msgId).originalId(originalMsgId).requestType(requestType);
      return response;
   }
}
