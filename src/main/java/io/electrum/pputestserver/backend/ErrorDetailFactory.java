package io.electrum.pputestserver.backend;

import java.util.UUID;

import javax.ws.rs.core.Response;

import io.electrum.pputestserver.validation.ValidationResult;
import io.electrum.vas.model.ErrorDetail;

public class ErrorDetailFactory {
   public static Response getIllFormattedMessageErrorDetail(ValidationResult result) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.FORMAT_ERROR);
      errorDetail.setErrorMessage("See error detail for format errors");
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

   public static Response getUnknownMeterIdErrorDetail(String meterId) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.UNKNOWN_CUSTOMER_ACCOUNT);
      errorDetail.setErrorMessage("Unknown meter ID");
      errorDetail.setDetailMessage("This meter ID was not recognised by the system: " + meterId);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }
   
   public static Response getNoTestCaseForMeterId(String message) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.GENERAL_ERROR);
      errorDetail.setErrorMessage("No test case associated with meter ID");
      errorDetail.setDetailMessage(message);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }

   public static Response getOriginalRequestNotFound(UUID originalReqId) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.UNABLE_TO_LOCATE_RECORD);
      errorDetail.setErrorMessage("Original request not found");
      errorDetail.setDetailMessage("The request with ID " + originalReqId + " could not be located on the system");
      return Response.status(Response.Status.NOT_FOUND).entity(errorDetail).build();
   }
}
