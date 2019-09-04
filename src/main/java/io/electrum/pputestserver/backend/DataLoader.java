package io.electrum.pputestserver.backend;

import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.prepaidutility.model.Utility;
import io.electrum.vas.model.Customer;
import io.electrum.vas.model.LedgerAmount;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataLoader {

   private static Logger logger = LoggerFactory.getLogger(DataLoader.class);

   /**
    * Reads a csv file to populate mock meter db
    * 
    * @param mapToLoad
    * @param inputFileName
    * @throws IOException
    */
   public static void loadMeterData(
         HashMap<String, MeterLookupResponse> mapToLoad,
         HashMap<String, ErrorDetail> errorMapToLoad,
         String inputFileName)
         throws IOException {
      logger.info("Loading data from file: {}", inputFileName);

      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream is = classloader.getResourceAsStream(inputFileName);

      CsvReader csv = new CsvReader(is, StandardCharsets.UTF_8);

      MeterLookupResponse response;
      try {
         csv.readHeaders();

         while (csv.readRecord()) {
            String meterId = csv.get("meterId");

            String shouldFail = csv.get("shouldFail");

            if (Boolean.parseBoolean(shouldFail)) {
               ErrorDetail errorDetail = new ErrorDetail();
               errorDetail.setErrorType(ErrorDetail.ErrorType.valueOf(csv.get("errorType")));
               errorDetail.setErrorMessage(csv.get("errorMessage"));
               errorMapToLoad.put(meterId, errorDetail);
            } else {
               String supplyGroupCode = csv.get("supplyGroupCode");
               String keyRevisionNum = csv.get("keyRevisionNum");
               String tariffIndex = csv.get("tariffIndex");
               String tokenTechCode = csv.get("tokenTechCode");
               String firstName = csv.get("firstName");
               String lastName = csv.get("lastName");
               String address = csv.get("address");
               String utilName = csv.get("utilName");
               String utilAddress = csv.get("utilAddress");
               String vatRegNum = csv.get("vatRegNum");
               String clientId = csv.get("clientId");
               String utilMessage = csv.get("utilMessage");
               String maxAmount = csv.get("maxAmount");
               String minAmount = csv.get("minAmount");
               String currency = csv.get("currency");
               String bsstDue = csv.get("bsstDue");

               Meter meter = new Meter();
               meter.setMeterId(meterId);
               meter.setSupplyGroupCode(supplyGroupCode);
               meter.keyRevisionNum(keyRevisionNum);
               meter.tariffIndex(tariffIndex);
               meter.tokenTechCode(tokenTechCode);

               Customer customer = new Customer();
               customer.setFirstName(firstName);
               customer.setLastName(lastName);
               customer.setAddress(address);

               Utility utility = new Utility();
               utility.setName(utilName);
               utility.setAddress(utilAddress);
               utility.setVatRegNum(vatRegNum);
               utility.setClientId(clientId);
               utility.setMessage(utilMessage);

               LedgerAmount maxAmountLedg = new LedgerAmount();
               maxAmountLedg.setAmount("".equals(maxAmount) ? 0 : new Long(maxAmount));
               maxAmountLedg.setCurrency(currency);

               LedgerAmount minAmountLedg = new LedgerAmount();
               minAmountLedg.setAmount("".equals(minAmount) ? 0 : new Long(minAmount));
               minAmountLedg.setCurrency(csv.get(15));

               response = new MeterLookupResponse();
               response.setMeter(meter);
               response.setCustomer(customer);
               response.setUtility(utility);
               response.minAmount(minAmountLedg);
               response.maxAmount(maxAmountLedg);

               Boolean bsstDueBool = Boolean.valueOf(bsstDue.toUpperCase());
               response.bsstDue(bsstDueBool);
               mapToLoad.put(meter.getMeterId(), response);
            }
         }
      } catch (IOException e) {
         logger.error("Error reading from file: {}", inputFileName, e);
         throw e;
      } finally {
         is.close();
         csv.close();
      }

      logger.info("{} entries successfully loaded from file", mapToLoad.size());
   }

   /**
    * Reads a json file for each meterId in the mock meter db and loads is into the mock response db. The file must
    * contain data for a specific test scenario and its name must be in the form <code>meterId</code>.json.
    * 
    * @param meters
    * @param mapToLoad
    * @throws IOException
    */
   public static void loadPurchaseResponses(
         HashMap<String, MeterLookupResponse> meters,
         HashMap<String, PurchaseResponse> mapToLoad)
         throws IOException {
      ObjectMapper mapper = Utils.getObjectMapper();

      for (String meterId : meters.keySet()) {
         PurchaseResponse response = new PurchaseResponse();
         FileInputStream inputStream = null;

         try {
            // TODO: make the path configurable
            inputStream = new FileInputStream("src/main/resources/" + meterId + ".json");
            response = mapper.readValue(inputStream, PurchaseResponse.class);
         } catch (Exception e) {
            logger.error("Error reading input file: {}", meterId + ".json");
            e.printStackTrace();
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }

         logger.info("Loading data from file: {}", meterId + ".json");

         // Set meter, customer, utility info
         response.setMeter(meters.get(meterId).getMeter());
         response.setCustomer(meters.get(meterId).getCustomer());
         response.setUtility(meters.get(meterId).getUtility());

         mapToLoad.put(meterId, response);
      }
   }
}
