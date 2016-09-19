package io.electrum.pputestserver.backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.prepaidutility.model.Utility;
import io.electrum.vas.model.Customer;
import io.electrum.vas.model.LedgerAmount;

public class DataLoader {

   private static Logger logger = LoggerFactory.getLogger(DataLoader.class);

   /**
    * Reads a csv file to populate mock meter db
    * 
    * @param mapToLoad
    * @param inputFileName
    * @throws IOException
    */
   public static void loadMeterData(HashMap<String, MeterLookupResponse> mapToLoad, String inputFileName)
         throws IOException {
      logger.info("Loading data from file: {}", inputFileName);

      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream is = classloader.getResourceAsStream(inputFileName);

      CsvReader csv = new CsvReader(is, Charset.forName("UTF-8"));

      MeterLookupResponse response;
      try {
         csv.readHeaders();

         while (csv.readRecord()) {
            Meter meter = new Meter();
            meter.setMeterId(csv.get(0));
            meter.setSupplyGroupCode(csv.get(1));
            meter.keyRevisionNum(csv.get(2));
            meter.tariffIndex(csv.get(3));
            meter.tokenTechCode(csv.get(4));

            Customer customer = new Customer();
            customer.setFirstName(csv.get(5));
            customer.setLastName(csv.get(6));
            customer.setAddress(csv.get(7));

            Utility utility = new Utility();
            utility.setName(csv.get(8));
            utility.setAddress(csv.get(9));
            utility.setVatRegNum(csv.get(10));
            utility.setClientId(csv.get(11));
            utility.setMessage(csv.get(12));

            LedgerAmount testCaseAmount = new LedgerAmount();
            testCaseAmount.setAmount(new Long(csv.get(13)));
            testCaseAmount.setCurrency(csv.get(14));

            response = new MeterLookupResponse();
            response.setMeter(meter);
            response.setCustomer(customer);
            response.setUtility(utility);
            response.minAmount(testCaseAmount);
            response.maxAmount(testCaseAmount);

            mapToLoad.put(meter.getMeterId(), response);
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
         HashMap<String, PurchaseResponse> mapToLoad) throws IOException {
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

   public static void loadFaultDescriptions(HashMap<String, String> descriptions) {
      
   }

}
