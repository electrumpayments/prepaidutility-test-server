package io.electrum.pputestserver.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.ws.rs.container.AsyncResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.resources.MeterLookupsResourceImpl;
import io.electrum.pputestserver.validation.RequestMessageValidator;
import io.electrum.pputestserver.validation.ValidationResult;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseRequest;

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

   public static void copyBaseFieldsFromRequest(MeterLookupResponse responseBody, MeterLookupRequest requestBody) {
      responseBody.setId(requestBody.getId());
      responseBody.setTime(requestBody.getTime());
      responseBody.setOriginator(requestBody.getOriginator());
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

   public static void validateRequest(MeterLookupRequest requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = RequestMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         logger.error("Invalid request message format");
         asyncResponse.resume(ErrorDetailFactory.getIllFormattedMessageErrorDetail(validation));
         return;
      }
   }
   
   public static void validateRequest(PurchaseRequest requestBody, AsyncResponse asyncResponse) {
      ValidationResult validation = RequestMessageValidator.validate(requestBody);

      if (!validation.isValid()) {
         logger.error("Invalid request message format");
         asyncResponse.resume(ErrorDetailFactory.getIllFormattedMessageErrorDetail(validation));
         return;
      }
   }
}
