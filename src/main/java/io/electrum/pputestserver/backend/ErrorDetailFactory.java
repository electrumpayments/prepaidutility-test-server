package io.electrum.pputestserver.backend;

import io.electrum.pputestserver.validation.ValidationResult;
import io.electrum.prepaidutility.model.ErrorDetail;

import javax.ws.rs.core.Response;

public class ErrorDetailFactory {
   public static Response getIllFormattedMessageErrorDetail(ValidationResult result) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.FORMAT_ERROR);
      errorDetail.setErrorMessage("See error detail for format errors");
      errorDetail.setDetailMessage((result == null ? "Mandatory fields missing." : result.getViolations()));
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }

   public static Response getNotUniqueUuidErrorDetail(String id) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.DUPLICATE_RECORD);
      errorDetail.setErrorMessage("Message ID (UUID) is not unique.");
      errorDetail.setDetailMessage(id);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }

   public static Response getInconsistentUuidErrorDetail(String pathId, String bodyUuid) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.FORMAT_ERROR);
      errorDetail.setErrorMessage("Message ID (UUID) is not the same as ID path parameter.");
      errorDetail.setDetailMessage("Message ID: " + bodyUuid + "; ID path parameter: " + pathId);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }

   public static Response getUnknownMeterIdErrorDetail(String meterId) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.UNKNOWN_METER_ID);
      errorDetail.setErrorMessage("Unknown meter ID");
      errorDetail.setDetailMessage("This meter ID was not recognised by the system: " + meterId);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }

   public static Response getUnknownMeterIdErrorDetail(String meterId, Response.Status status) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.UNKNOWN_METER_ID);
      errorDetail.setErrorMessage("Unknown meter ID");
      errorDetail.setDetailMessage("This meter ID was not recognised by the system: " + meterId);
      return Response.status(status).entity(errorDetail).build();
   }

   public static Response getInvalidRequestAmount() {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.INVALID_AMOUNT);
      errorDetail.setErrorMessage("Invalid request amount");
      errorDetail.setDetailMessage(
            "Requested purchase amount does not correspond to the amount specified for this test case. See minAmount and maxAmount in meter lookup response for allowed values.");
      return Response.status(Response.Status.BAD_REQUEST).entity(errorDetail).build();
   }

   public static Response getUpstreamTimeout() {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.UPSTREAM_UNAVAILABLE);
      errorDetail.setErrorMessage("Timeout");
      errorDetail.setDetailMessage("No response from upstream service");
      return Response.status(Response.Status.GATEWAY_TIMEOUT).entity(errorDetail).build();
   }

   public static Response getInternalServerError(String message) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.GENERAL_ERROR);
      errorDetail.setErrorMessage(message);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDetail).build();
   }

   public static Response getOriginalRequestNotFound(String originalReqId) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setErrorType(ErrorDetail.ErrorType.UNABLE_TO_LOCATE_RECORD);
      errorDetail.setErrorMessage("Original request not found");
      errorDetail.setDetailMessage("The request with ID " + originalReqId + " could not be located on the system");
      return Response.status(Response.Status.NOT_FOUND).entity(errorDetail).build();
   }
}
