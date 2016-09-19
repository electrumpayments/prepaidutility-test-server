package io.electrum.pputestserver.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import io.electrum.prepaidutility.model.FaultReportRequest;
import io.electrum.prepaidutility.model.KeyChangeTokenRequest;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.vas.model.BasicReversal;
import io.electrum.vas.model.Institution;
import io.electrum.vas.model.LedgerAmount;
import io.electrum.vas.model.Merchant;
import io.electrum.vas.model.MerchantName;
import io.electrum.vas.model.Originator;
import io.electrum.vas.model.Tender;
import io.electrum.vas.model.TenderAdvice;
import io.electrum.vas.model.ThirdPartyIdentifier;
import io.electrum.vas.model.Transaction;

public class RequestMessageValidator {

   private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

   public static ValidationResult validate(MeterLookupRequest request) {
      ValidationResult result = new ValidationResult();

      if (isEmpty(request)) {
         result.addViolation(new RequestMessageViolation("message", "", "", null));
         return result;
      }

      validate(request, result);

      return result;
   }

   public static ValidationResult validate(PurchaseRequest request) {
      ValidationResult result = new ValidationResult();

      if (isEmpty(request)) {
         result.addViolation(new RequestMessageViolation("message", "", "", null));
         return result;
      }

      validate(request, result);

      return result;
   }

   public static ValidationResult validate(KeyChangeTokenRequest request) {
      ValidationResult result = new ValidationResult();

      if (isEmpty(request)) {
         result.addViolation(new RequestMessageViolation("message", "", "", null));
         return result;
      }

      validate(request, result);

      return result;
   }

   public static ValidationResult validate(FaultReportRequest request) {
      ValidationResult result = new ValidationResult();

      if (isEmpty(request)) {
         result.addViolation(new RequestMessageViolation("message", "", "", null));
         return result;
      }

      validate(request, result);

      return result;
   }

   public static ValidationResult validate(TenderAdvice request) {
      ValidationResult result = new ValidationResult();

      if (isEmpty(request)) {
         result.addViolation(new RequestMessageViolation("message", "", "", null));
         return result;
      }

      validate(request, result);

      return result;
   }
   
   public static ValidationResult validate(BasicReversal request) {
      ValidationResult result = new ValidationResult();

      if (isEmpty(request)) {
         result.addViolation(new RequestMessageViolation("message", "", "", null));
         return result;
      }

      validate(request, result);

      return result;
   }

   private static <T extends Transaction> void validateBasicTranFields(T request, ValidationResult result) {
      validateValue(request, "message", "id", result);
      validateValue(request, "message", "time", result);
      validateValue(request, "message", "originator", result);
      validate(request.getOriginator(), result);
      validateValue(request, "message", "settlementEntity", result);
      validate(request.getSettlementEntity(), result);
      validateValue(request, "message", "receiver", result);
      validate(request.getReceiver(), result);
      validateValue(request, "message", "client", result);
      validate(request.getClient(), result);
      validateValue(request, "message", "thirdPartyIdentifiers", result);
      validate(request.getThirdPartyIdentifiers(), result);
   }

   private static void validate(MeterLookupRequest request, ValidationResult result) {
      validateBasicTranFields(request, result);

      validate(request.getMeter(), result);
   }

   private static void validate(PurchaseRequest request, ValidationResult result) {
      validateBasicTranFields(request, result);

      validate(request.getMeter(), result);
      validate(request.getPurchaseAmount(), "purchaseAmount", result);
   }

   private static void validate(KeyChangeTokenRequest request, ValidationResult result) {
      validateBasicTranFields(request, result);

      validate(request.getMeter(), result);
   }

   private static void validate(FaultReportRequest request, ValidationResult result) {
      validateBasicTranFields(request, result);

      validate(request.getMeter(), result);
      validateValue(request, "message", "contactNumber", result);
      validateValue(request, "message", "faultType", result);
   }

   private static void validate(TenderAdvice request, ValidationResult result) {
      validateValue(request, "message", "id", result);
      validateValue(request, "message", "requestId", result);
      validateValue(request, "message", "time", result);
      validateTenders(request.getTenders(), result);
      validate(request.getThirdPartyIdentifiers(), result);
   }
   
   private static void validate(BasicReversal request, ValidationResult result) {
      validateValue(request, "message", "id", result);
      validateValue(request, "message", "requestId", result);
      validateValue(request, "message", "time", result);
      validateValue(request, "message", "reversalReason", result);
      validate(request.getThirdPartyIdentifiers(), result);
   }

   private static void validate(LedgerAmount amount, String fieldName, ValidationResult result) {
      if (isEmpty(amount)) {
         return;
      }

      validateValue(amount, fieldName, "amount", result);
      validateValue(amount, fieldName, "currency", result);
      validateValue(amount, fieldName, "ledgerIndicator", result);
   }

   private static void validate(Meter meter, ValidationResult result) {
      if (isEmpty(meter)) {
         return;
      }

      validateValue(meter, "meter", "meterId", result);
      validateValue(meter, "meter", "track2Data", result);
      validateValue(meter, "meter", "serviceType", result);
      validateValue(meter, "meter", "supplyGroupCode", result);
      validateValue(meter, "meter", "keyRevisionNum", result);
      validateValue(meter, "meter", "tariffIndex", result);
      validateValue(meter, "meter", "tokenTechCode", result);
      validateValue(meter, "meter", "algorithmCode", result);
   }

   private static void validate(Originator originator, ValidationResult result) {
      if (isEmpty(originator)) {
         return;
      }

      validateValue(originator, "originator", "terminalId", result);
      validateValue(originator, "originator", "institution", result);
      validate(originator.getInstitution(), result);
      validateValue(originator, "originator", "merchant", result);
      validate(originator.getMerchant(), result);

   }

   private static void validate(ThirdPartyIdentifier thirdPartyIdentifier, ValidationResult result) {
      if (isEmpty(thirdPartyIdentifier)) {
         return;
      }

      validateValue(thirdPartyIdentifier, "thirdPartyIdentifier", "institutionId", result);
      validateValue(thirdPartyIdentifier, "thirdPartyIdentifier", "transactionIdentifier", result);

   }

   private static void validate(Tender tender, ValidationResult result) {
      if (isEmpty(tender)) {
         return;
      }

      validateValue(tender, "tender", "accountType", result);
      validate(tender.getAmount(), "amount", result);
      validateValue(tender, "tender", "cardNumber", result);
      validateValue(tender, "tender", "reference", result);
      validateValue(tender, "tender", "tenderType", result);

   }

   private static void validate(List<ThirdPartyIdentifier> thirdPartyIdentifiers, ValidationResult result) {
      if (isEmpty(thirdPartyIdentifiers)) {
         return;
      }

      for (ThirdPartyIdentifier thirdPartyIdentifier : thirdPartyIdentifiers) {
         validate(thirdPartyIdentifier, result);
      }
   }

   private static void validateTenders(List<Tender> tenders, ValidationResult result) {
      if (isEmpty(tenders)) {
         return;
      }

      for (Tender tender : tenders) {
         validate(tender, result);
      }
   }

   private static void validate(Institution institution, ValidationResult result) {
      if (isEmpty(institution)) {
         return;
      }

      validateValue(institution, "institution", "id", result);
      validateValue(institution, "institution", "name", result);

   }

   private static void validate(Merchant merchant, ValidationResult result) {
      if (isEmpty(merchant)) {
         return;
      }

      validateValue(merchant, "merchant", "merchantType", result);
      validateValue(merchant, "merchant", "merchantId", result);
      validateValue(merchant, "merchant", "merchantName", result);
      validate(merchant.getMerchantName(), result);
   }

   private static void validate(MerchantName merchantName, ValidationResult result) {
      if (isEmpty(merchantName)) {
         return;
      }

      validateValue(merchantName, "merchantName", "name", result);
      validateValue(merchantName, "merchantName", "city", result);
      validateValue(merchantName, "merchantName", "region", result);
      validateValue(merchantName, "merchantName", "country", result);
   }

   @SuppressWarnings("rawtypes")
   private static boolean isEmpty(Object o) {

      if (o == null) {
         return true;
      }

      if (o instanceof String) {
         return ((String) o).isEmpty();
      }

      if (o instanceof List) {
         return ((List) o).isEmpty();
      }

      return false;
   }

   private static <T> Set<ConstraintViolation<T>> validateValue(
         T tInstance,
         String parent,
         String propertyName,
         ValidationResult validationResult) {
      if (tInstance == null) {
         return new HashSet<ConstraintViolation<T>>();
      }

      Set<ConstraintViolation<T>> violations = validator.validateProperty(tInstance, propertyName);

      for (ConstraintViolation<T> constraintViolation : violations) {
         validationResult.addViolation(
               new RequestMessageViolation(
                     parent,
                     propertyName,
                     constraintViolation.getMessage(),
                     constraintViolation.getInvalidValue()));
      }

      return violations;
   }
}
