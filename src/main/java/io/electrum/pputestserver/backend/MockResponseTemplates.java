package io.electrum.pputestserver.backend;

import io.electrum.pputestserver.backend.builders.ResponseBuilder;
import io.electrum.pputestserver.backend.scenarios.dynamic.DynamicDataLoader;
import io.electrum.pputestserver.backend.scenarios.error.ErrorDataLoader;
import io.electrum.pputestserver.backend.scenarios.normal.DataLoader;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.KeyChangeTokenResponse;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Contains predefined response message objects corresponding to specific test scenarios. The meter ID in the request
 * determines which response object is returned by the server.
 * 
 * Response objects for each type are stored in a {@link HashMap} referenced by unique meter ID.
 *
 */
public class MockResponseTemplates {

   private static Logger logger = LoggerFactory.getLogger(DataLoader.class);

   private static HashMap<String, MeterLookupResponse> meterLookupResponses = new HashMap<>();
   private static HashMap<String, ErrorDetail> errorDetailResponses = new HashMap<>();
   private static HashMap<String, PurchaseResponse> purchaseResponses = new HashMap<>();
   private static HashMap<String, HashMap<String, ResponseBuilder<?>>> dynamicResponses = new HashMap<>();
   private static HashMap<String, PurchaseResponse> partialPurchaseResponses = new HashMap<>();

   public static MeterLookupResponse getMeterLookupResponse(String meterId) {
      return meterLookupResponses.get(meterId);
   }

   public static boolean meterExists(String meterId) {
      return meterLookupResponses.containsKey(meterId);
   }

   public static PurchaseResponse getPurchaseResponse(String meterId) {
      return purchaseResponses.get(meterId);
   }

   public static PurchaseResponse getPartialPurchaseResponse(String meterId) {
      return partialPurchaseResponses.get(meterId);
   }

   public static ResponseBuilder<?> getDynamicResponseBuilder(String meterId, String type) {
      return dynamicResponses.get(meterId).get(type);
   }

   public static Optional<ErrorDetail> getErrorDetailResponse(String meterId) {
      return Optional.ofNullable(errorDetailResponses.get(meterId));
   }

   public static boolean isErrorScenario(String meterId) {
      return errorDetailResponses.containsKey(meterId);
   }

   public static boolean isDynamicScenario(String meterId, String type) {
      return dynamicResponses.containsKey(meterId) && dynamicResponses.get(meterId).containsKey(type);
   }

   public static KeyChangeTokenResponse getKctResponse() throws IOException {
      return readKctResponseFromFile();
   }

   public static void init()
         throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
         InstantiationException, IllegalAccessException {
      DataLoader.loadMeterData(meterLookupResponses, "meters.csv");
      ErrorDataLoader.loadMeterData(errorDetailResponses, "errorMeters.csv");
      DataLoader.loadPurchaseResponses(meterLookupResponses, purchaseResponses);
      DynamicDataLoader.loadMeterData(dynamicResponses, "dynamicMeters.csv");
      DynamicDataLoader.loadPartialPurchaseResponses(dynamicResponses.keySet(), partialPurchaseResponses);
   }

   private static KeyChangeTokenResponse readKctResponseFromFile() throws IOException {
      ObjectMapper mapper = Utils.getObjectMapper();
      KeyChangeTokenResponse response = new KeyChangeTokenResponse();
      FileInputStream inputStream = null;

      try {
         inputStream = new FileInputStream("src/main/resources/" + "KeyChangeToken.json");
         response = mapper.readValue(inputStream, KeyChangeTokenResponse.class);
      } catch (Exception e) {
         logger.error("Error reading input file: {}", "KeyChangeToken.json");
         e.printStackTrace();
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }

      return response;
   }
}
