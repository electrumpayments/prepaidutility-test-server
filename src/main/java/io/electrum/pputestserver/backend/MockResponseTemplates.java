package io.electrum.pputestserver.backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.KeyChangeTokenResponse;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseResponse;

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
   private static HashMap<String, PurchaseResponse> purchaseResponses = new HashMap<>();

   public static MeterLookupResponse getMeterLookupResponse(String meterId) {
      return meterLookupResponses.get(meterId);
   }
   
   public static boolean meterExists(String meterId) {
      return meterLookupResponses.containsKey(meterId);
   }

   public static PurchaseResponse getPurchaseResponse(String meterId) {
      return purchaseResponses.get(meterId);
   }

   public static KeyChangeTokenResponse getKctResponse() throws IOException {
      return readKctResponseFromFile();
   }

   public static void init() throws IOException {
      DataLoader.loadMeterData(meterLookupResponses, "meters.csv");
      DataLoader.loadPurchaseResponses(meterLookupResponses, purchaseResponses);
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
