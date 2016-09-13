package io.electrum.pputestserver.backend;

import java.io.IOException;
import java.util.HashMap;

import io.electrum.prepaidutility.model.FaultReportResponse;
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

   private static HashMap<String, MeterLookupResponse> meterLookupResponses = new HashMap<>();
   private static HashMap<String, PurchaseResponse> purchaseResponses = new HashMap<>();
   private static HashMap<String, KeyChangeTokenResponse> kctResponses = new HashMap<>();
   private static HashMap<String, FaultReportResponse> faultReportResponses = new HashMap<>();

   public static MeterLookupResponse getMeterLookupResponse(String meterId) {
      return meterLookupResponses.get(meterId);
   }

   public static PurchaseResponse getPurchaseResponse(String meterId) {
      return purchaseResponses.get(meterId);
   }

   public static KeyChangeTokenResponse getKctResponse(String meterId) {
      return kctResponses.get(meterId);
   }

   public static FaultReportResponse getFaultReportResponse(String meterId) {
      return faultReportResponses.get(meterId);
   }

   public static void init() throws IOException {
      DataLoader.loadMeterData(meterLookupResponses, "meters.csv");
   }
}
