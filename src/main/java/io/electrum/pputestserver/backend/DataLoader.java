package io.electrum.pputestserver.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.prepaidutility.model.Utility;
import io.electrum.vas.model.Customer;

public class DataLoader {

   private static Logger logger = LoggerFactory.getLogger(DataLoader.class);

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

            response = new MeterLookupResponse();
            response.setMeter(meter);
            response.setCustomer(customer);
            response.setUtility(utility);

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

   public static void loadPurchaseData(HashMap<String, PurchaseResponse> mapToLoad, String inputFileName) {
      // TODO
   }

}
