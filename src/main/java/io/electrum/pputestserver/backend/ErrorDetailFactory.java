package io.electrum.pputestserver.backend;

import java.util.UUID;

import javax.ws.rs.core.Response;

import io.electrum.pputestserver.validation.ValidationResult;
import io.electrum.vas.model.ErrorDetail;

public class ErrorDetailFactory {
   public static Response getIllFormattedMessageErrorDetail(ValidationResult result) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.FORMAT_ERROR);
      errorDetail.setErrorMessage("See error detail for format errors.");
      errorDetail.setDetailMessage(
            (result == null ? "Mandatory fields missing - check server logs." : result.getViolations()));
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }
   
   public static Response getNotUniqueUuidErrorDetail(UUID id) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.DUPLICATE_RECORD);
      errorDetail.setErrorMessage("Message ID (UUID) is not unique.");
      errorDetail.setDetailMessage(id);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }
}
