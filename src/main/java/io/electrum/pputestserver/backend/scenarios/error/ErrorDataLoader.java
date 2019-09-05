package io.electrum.pputestserver.backend.scenarios.error;

import io.electrum.prepaidutility.model.ErrorDetail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

public class ErrorDataLoader {

   private static Logger logger = LoggerFactory.getLogger(ErrorDataLoader.class);

   /**
    * Reads a csv file to populate mock meter db
    *
    * @param inputFileName
    * @throws IOException
    */
   public static void loadMeterData(HashMap<String, ErrorDetail> errorMapToLoad, String inputFileName)
         throws IOException {
      logger.info("Loading error data from file: {}", inputFileName);

      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream is = classloader.getResourceAsStream(inputFileName);

      CsvReader csv = new CsvReader(is, StandardCharsets.UTF_8);

      try {
         csv.readHeaders();
         while (csv.readRecord()) {
            String meterId = csv.get("meterId");
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setErrorType(ErrorDetail.ErrorType.valueOf(csv.get("errorType")));
            errorDetail.setErrorMessage(csv.get("errorMessage"));
            errorMapToLoad.put(meterId, errorDetail);
         }
      } catch (IOException e) {
         logger.error("Error reading from file: {}", inputFileName, e);
         throw e;
      } finally {
         is.close();
         csv.close();
      }

      logger.info("{} error entries successfully loaded from file", errorMapToLoad.size());
   }
}
