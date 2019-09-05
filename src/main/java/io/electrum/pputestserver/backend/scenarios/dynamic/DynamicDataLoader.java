package io.electrum.pputestserver.backend.scenarios.dynamic;

import io.electrum.pputestserver.backend.builders.ResponseBuilder;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.PurchaseResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamicDataLoader {

   private static Logger logger = LoggerFactory.getLogger(DynamicDataLoader.class);

   /**
    * Reads a csv file to populate mock meter db
    *
    * @param inputFileName
    * @throws IOException
    */
   public static void loadMeterData(
         HashMap<String, HashMap<String, ResponseBuilder<?>>> responseMapToLoad,
         String inputFileName)
         throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
         InvocationTargetException, InstantiationException {
      logger.info("Loading error data from file: {}", inputFileName);

      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream is = classloader.getResourceAsStream(inputFileName);

      CsvReader csv = new CsvReader(is, StandardCharsets.UTF_8);

      try {
         csv.readHeaders();
         while (csv.readRecord()) {
            String meterId = csv.get("meterId");
            String responseBuilderDetails = csv.get("responseBuilders");
            for (String typeAndClass : responseBuilderDetails.split("\\|")) {
               String[] typeAndClassArr = typeAndClass.split(":");
               String type = typeAndClassArr[0];
               String clazz = typeAndClassArr[1];
               ResponseBuilder<?> responseBuilder =
                     (ResponseBuilder<?>) ClassLoader.getSystemClassLoader()
                           .loadClass(clazz)
                           .getDeclaredConstructor()
                           .newInstance();
               if (responseMapToLoad.get(meterId) == null) {
                  HashMap<String, ResponseBuilder<?>> map = new HashMap<>();
                  map.put(type, responseBuilder);
                  responseMapToLoad.put(meterId, map);
               } else {
                  responseMapToLoad.get(meterId).put(type, responseBuilder);
               }
            }
         }
      } catch (IOException e) {
         logger.error("Error reading from file: {}", inputFileName, e);
         throw e;
      } finally {
         is.close();
         csv.close();
      }

      logger.info("{} error entries successfully loaded from file", responseMapToLoad.size());
   }

   public static void loadPartialPurchaseResponses(Set<String> meters, HashMap<String, PurchaseResponse> mapToLoad)
         throws IOException {
      ObjectMapper mapper = Utils.getObjectMapper();

      for (String meterId : meters) {
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

         mapToLoad.put(meterId, response);
      }
   }
}
