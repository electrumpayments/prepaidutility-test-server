package io.electrum.pputestserver.utils;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.resources.MeterLookupsResourceImpl;
import io.electrum.pputestserver.validation.IncomingMessageValidator;
import io.electrum.pputestserver.validation.ValidationResult;
import io.electrum.prepaidutility.model.ConfirmationAdvice;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.FaultReportRequest;
import io.electrum.prepaidutility.model.FaultReportResponse;
import io.electrum.prepaidutility.model.KeyChangeTokenRequest;
import io.electrum.prepaidutility.model.KeyChangeTokenResponse;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.prepaidutility.model.PurchaseRequestRetry;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.prepaidutility.model.ReversalAdvice;
import io.electrum.vas.model.BasicAdvice;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.TenderAdvice;
import io.electrum.vas.model.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class Utils {
   private static final Logger logger = LoggerFactory.getLogger(MeterLookupsResourceImpl.class);

   public static String objectToPrettyPrintedJson(Object object) throws JsonProcessingException {
      ObjectWriter objectWriter = getObjectMapper().writer();
      return objectWriter.withDefaultPrettyPrinter().writeValueAsString(object);
   }

   public static ObjectMapper getObjectMapper() {
      final ObjectMapper objectMapper = new ObjectMapper();

      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
      objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
      objectMapper.registerModule(new JodaModule());
      objectMapper.setDateFormat(getDefaultDateFormat());

      return objectMapper;
   }

   public static DateFormat getDefaultDateFormat() {
      // Use RFC3339 format for date and datetime.
      // See http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return dateFormat;
   }

   public static <T extends Transaction> void copyBaseFieldsFromRequest(T responseBody, T requestBody) {
      responseBody.setId(requestBody.getId());
      responseBody.setTime(requestBody.getTime());
      responseBody.setOriginator(requestBody.getOriginator());
      responseBody.setReceiver(requestBody.getReceiver());
      responseBody.setSettlementEntity(requestBody.getSettlementEntity());
      responseBody.setClient(requestBody.getClient());
      responseBody.setThirdPartyIdentifiers(requestBody.getThirdPartyIdentifiers());
   }

   public static void logMessageTrace(Object jsonObject) {
      try {
         logger.debug(
               System.lineSeparator() + jsonObject.getClass().getSimpleName() + ":" + System.lineSeparator()
                     + Utils.objectToPrettyPrintedJson(jsonObject));
      } catch (JsonProcessingException e) {
         logger.error("Error processing JSON request message");
      }
   }

   public static boolean isUuidConsistent(String pathId, String bodyUuid) {
      if (pathId == null || bodyUuid == null) {
         return false;
      }
      return pathId.equals(bodyUuid);
   }

   public static boolean validateRequest(MeterLookupRequest requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   public static boolean validateRequest(PurchaseRequest requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   public static boolean validateRequest(PurchaseRequestRetry requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   public static boolean validateRequest(KeyChangeTokenRequest requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   public static boolean validateRequest(FaultReportRequest requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   public static boolean validateRequest(TenderAdvice requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   public static boolean validateRequest(BasicReversal requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = IncomingMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         sendErrorResponse(validation, asyncResponse);
         return false;
      }
      return true;
   }

   private static void sendErrorResponse(ValidationResult validation, AsyncResponse asyncResponse) {
      logger.error("Invalid request message format");
      asyncResponse.resume(ErrorDetailFactory.getIllFormattedMessageErrorDetail(validation));
   }

   public static ErrorDetail.RequestType determineRequestType(Object transaction) {
      if (transaction instanceof BasicAdvice && !(transaction instanceof ConfirmationAdvice)
            && !(transaction instanceof ReversalAdvice)) {
         // If it's a BasicAdvice or BasicAdviceResponse, we don't know what it was originally or is without the request
         // itself.
         throw new RuntimeException("Cannot determine RequestType");
      }

      if (transaction instanceof MeterLookupRequest || transaction instanceof MeterLookupResponse) {
         return ErrorDetail.RequestType.METER_LOOKUP_REQUEST;
      } else if (transaction instanceof PurchaseRequestRetry) {
         return ErrorDetail.RequestType.TOKEN_PURCHASE_RETRY_REQUEST;
      } else if (transaction instanceof PurchaseRequest || transaction instanceof PurchaseResponse) {
         return ErrorDetail.RequestType.TOKEN_PURCHASE_REQUEST;
      } else if (transaction instanceof KeyChangeTokenRequest || transaction instanceof KeyChangeTokenResponse) {
         return ErrorDetail.RequestType.KEY_CHANGE_TOKEN_REQUEST;
      } else if (transaction instanceof FaultReportRequest || transaction instanceof FaultReportResponse) {
         return ErrorDetail.RequestType.FAULT_REPORT_REQUEST;
      } else if (transaction instanceof ConfirmationAdvice) {
         return ErrorDetail.RequestType.CONFIRMATION_ADVICE;
      } else {
         return ErrorDetail.RequestType.REVERSAL_ADVICE;
      }
   }

   public static Response.Status getStatusFromErrorType(ErrorDetail.ErrorType errorType) {
      switch (errorType) {
      case DO_NOT_HONOR:
      case FORMAT_ERROR:
      case INVALID_AMOUNT:
      case INVALID_MSISDN:
      case DUPLICATE_RECORD:
      case INVALID_MERCHANT:
      case UNKNOWN_METER_ID:
      case INVALID_AN32_TOKEN:
      case INVALID_LOYALTY_CARD:
      case TRANSACTION_DECLINED:
      case FUNCTION_NOT_SUPPORTED:
      case TRANSACTION_NOT_SUPPORTED:
         return Response.Status.BAD_REQUEST;
      case UNABLE_TO_LOCATE_RECORD:
         return Response.Status.NOT_FOUND;
      case UPSTREAM_UNAVAILABLE:
         return Response.Status.GATEWAY_TIMEOUT;
      case GENERAL_ERROR:
      case ROUTING_ERROR:
      default:
         return Response.Status.INTERNAL_SERVER_ERROR;
      }
   }

   /**
    * Check that the request amount falls within the limits set for that meter.
    *
    * @param requestBody
    * @param meterId
    * @return false if the requested amount is outside of the min/max range for the meter
    */
   public static boolean isValidRequestAmount(PurchaseRequest requestBody, String meterId) {
      if (MockResponseTemplates.getMeterLookupResponse(meterId) == null) {
         logger.error("Failed to get meter info for this test case (meter ID: {})", meterId);
         return false;
      }

      if (MockResponseTemplates.getMeterLookupResponse(meterId).getMinAmount() == null) {
         logger.error("Failed to get minimum request amount for this test case (meter ID: {})", meterId);
         return false;
      }

      if (MockResponseTemplates.getMeterLookupResponse(meterId).getMaxAmount().getAmount() == null) {
         logger.error("Failed to get maximum request amount for this test case (meter ID: {})", meterId);
         return false;
      }

      Long minAmount = MockResponseTemplates.getMeterLookupResponse(meterId).getMinAmount().getAmount();
      Long maxAmount = MockResponseTemplates.getMeterLookupResponse(meterId).getMaxAmount().getAmount();
      Long requestAmount = requestBody.getPurchaseAmount().getAmount();

      return requestAmount.compareTo(minAmount) >= 0 && requestAmount.compareTo(maxAmount) <= 0;
   }
}
