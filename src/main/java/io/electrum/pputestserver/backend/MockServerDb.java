package io.electrum.pputestserver.backend;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.electrum.prepaidutility.model.FaultReportRequest;
import io.electrum.prepaidutility.model.KeyChangeTokenRequest;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.vas.model.BasicAdvice;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.TenderAdvice;
import io.electrum.vas.model.Transaction;

/**
 * A mock REST server in-memory database represented by {@link Cache} objects.
 * 
 * Resources are created by caching incoming POST requests by their {@link UUID}. To restrict memory footprint, entries
 * are removed after specified length.
 * 
 */
public class MockServerDb {
   private static final Logger logger = LoggerFactory.getLogger(MockServerDb.class);

   private static int expireAfterMinutes = 30;

   private static Cache<UUID, MeterLookupRequest> meterLookups = buildNewCache();
   private static Cache<UUID, PurchaseRequest> purchaseRequests = buildNewCache();
   private static Cache<UUID, KeyChangeTokenRequest> kctRequests = buildNewCache();
   private static Cache<UUID, FaultReportRequest> faultReportRequests = buildNewCache();

   private static Cache<UUID, TenderAdvice> confirmationRequests = buildNewAdviceCache();
   private static Cache<UUID, BasicReversal> reversalRequests = buildNewAdviceCache();

   public static MeterLookupRequest getMeterLookup(UUID uuid) {
      return meterLookups.getIfPresent(uuid);
   }

   public static PurchaseRequest getPurchaseRequest(UUID uuid) {
      return purchaseRequests.getIfPresent(uuid);
   }

   public static KeyChangeTokenRequest getKctRequest(UUID uuid) {
      return kctRequests.getIfPresent(uuid);
   }

   public static FaultReportRequest getFaultReportRequest(UUID uuid) {
      return faultReportRequests.getIfPresent(uuid);
   }

   public static TenderAdvice getConfirmationRequest(UUID uuid) {
      return confirmationRequests.getIfPresent(uuid);
   }

   public static BasicReversal getReversalRequest(UUID uuid) {
      return reversalRequests.getIfPresent(uuid);
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

   public static boolean add(TenderAdvice request) {
      if (confirmationRequests.asMap().containsKey(request.getId())) {
         return false;
      }

      confirmationRequests.put(request.getId(), request);
      return true;
   }

   public static boolean add(BasicReversal request) {
      if (reversalRequests.asMap().containsKey(request.getId())) {
         return false;
      }

      reversalRequests.put(request.getId(), request);
      return true;
   }

   private static <T extends Transaction> Cache<UUID, T> buildNewCache() {
      return CacheBuilder.newBuilder().expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES).build();
   }

   private static <T extends BasicAdvice> Cache<UUID, T> buildNewAdviceCache() {
      return CacheBuilder.newBuilder().expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES).build();
   }
}
