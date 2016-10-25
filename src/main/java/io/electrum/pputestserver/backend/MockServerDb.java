package io.electrum.pputestserver.backend;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.electrum.prepaidutility.model.ConfirmationAdvice;
import io.electrum.prepaidutility.model.FaultReportRequest;
import io.electrum.prepaidutility.model.KeyChangeTokenRequest;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.prepaidutility.model.PurchaseRequestRetry;
import io.electrum.prepaidutility.model.ReversalAdvice;
import io.electrum.vas.model.BasicAdvice;
import io.electrum.vas.model.Transaction;

/**
 * A mock REST server in-memory database represented by {@link Cache} objects.
 * 
 * Resources are created by caching incoming POST requests by their UUID (which is stored as a {@link String}). To
 * restrict memory footprint, entries are removed after specified length.
 * 
 */
public class MockServerDb {
   private static int expireAfterMinutes = 30;

   private static Cache<String, MeterLookupRequest> meterLookups = buildNewCache();
   private static Cache<String, PurchaseRequest> purchaseRequests = buildNewCache();
   private static Cache<String, PurchaseRequestRetry> purchaseRequestRetries = buildNewRetryCache();
   private static Cache<String, KeyChangeTokenRequest> kctRequests = buildNewCache();
   private static Cache<String, FaultReportRequest> faultReportRequests = buildNewCache();

   private static Cache<String, ConfirmationAdvice> confirmations = buildNewAdviceCache();
   private static Cache<String, ReversalAdvice> reversals = buildNewAdviceCache();

   public static MeterLookupRequest getMeterLookup(String uuid) {
      return meterLookups.getIfPresent(uuid);
   }

   public static PurchaseRequest getPurchaseRequest(String uuid) {
      return purchaseRequests.getIfPresent(uuid);
   }

   public static PurchaseRequestRetry getPurchaseRequestRetry(String uuid) {
      return purchaseRequestRetries.getIfPresent(uuid);
   }

   public static KeyChangeTokenRequest getKctRequest(String uuid) {
      return kctRequests.getIfPresent(uuid);
   }

   public static FaultReportRequest getFaultReportRequest(String uuid) {
      return faultReportRequests.getIfPresent(uuid);
   }

   public static ConfirmationAdvice getConfirmationAdvice(String uuid) {
      return confirmations.getIfPresent(uuid);
   }

   public static ReversalAdvice getReversalAdvice(String uuid) {
      return reversals.getIfPresent(uuid);
   }

   public static boolean add(MeterLookupRequest request) {
      if (meterLookups.asMap().containsKey(request.getId())) {
         return false;
      }

      meterLookups.put(request.getId(), request);
      return true;
   }

   public static boolean add(PurchaseRequest request) {
      if (purchaseRequests.asMap().containsKey(request.getId())) {
         return false;
      }

      purchaseRequests.put(request.getId(), request);
      return true;
   }

   public static boolean add(PurchaseRequest request, String originalRequestId) {
      if (purchaseRequests.asMap().containsKey(originalRequestId)) {
         return false;
      }

      purchaseRequests.put(originalRequestId, request);
      return true;
   }

   public static boolean add(PurchaseRequestRetry request) {
      if (purchaseRequestRetries.asMap().containsKey(request.getRetryId())) {
         return false;
      }

      purchaseRequestRetries.put(request.getRetryId(), request);
      return true;
   }

   public static boolean add(KeyChangeTokenRequest request) {
      if (kctRequests.asMap().containsKey(request.getId())) {
         return false;
      }

      kctRequests.put(request.getId(), request);
      return true;
   }

   public static boolean add(FaultReportRequest request) {
      if (faultReportRequests.asMap().containsKey(request.getId())) {
         return false;
      }

      faultReportRequests.put(request.getId(), request);
      return true;
   }

   public static boolean add(ConfirmationAdvice request) {
      if (confirmations.asMap().containsKey(request.getId())) {
         return false;
      }

      confirmations.put(request.getId(), request);
      return true;
   }

   public static boolean add(ReversalAdvice request) {
      if (reversals.asMap().containsKey(request.getId())) {
         return false;
      }

      reversals.put(request.getId(), request);
      return true;
   }

   private static <T extends Transaction> Cache<String, T> buildNewCache() {
      return CacheBuilder.newBuilder().expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES).build();
   }

   private static Cache<String, PurchaseRequestRetry> buildNewRetryCache() {
      return CacheBuilder.newBuilder().expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES).build();
   }

   private static <T extends BasicAdvice> Cache<String, T> buildNewAdviceCache() {
      return CacheBuilder.newBuilder().expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES).build();
   }
}
