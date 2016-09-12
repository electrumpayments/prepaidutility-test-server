package io.electrum.pputestserver.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.electrum.pputestserver.backend.ErrorDetailFactory;
import io.electrum.pputestserver.backend.MockServerDb;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.pputestserver.validation.RequestMessageValidator;
import io.electrum.pputestserver.validation.ValidationResult;
import io.electrum.prepaidutility.api.IMeterLookupsResource;
import io.electrum.prepaidutility.api.MeterLookupsResource;
import io.electrum.prepaidutility.model.MeterLookupRequest;

@Path("/prepaidutility/v1/meterLookups")
public class MeterLookupsResourceImpl extends MeterLookupsResource implements IMeterLookupsResource {

   static MeterLookupsResourceImpl instance = null;
   private static final Logger logger = LoggerFactory.getLogger(MeterLookupsResourceImpl.class);

   @Override
   protected IMeterLookupsResource getResourceImplementation() {
      if (instance == null) {
         instance = new MeterLookupsResourceImpl();
      }
      return instance;
   }

   @Override
   public void createMeterLookup(
         String lookupId,
         MeterLookupRequest body,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo) {
      ValidationResult validation = RequestMessageValidator.validate(body);

      if (!validation.isValid()) {
         logger.error("Invalid request message format");
         asyncResponse.resume(ErrorDetailFactory.getIllFormattedMessageErrorDetail(validation));
         return;
      }

      try {
         logger.debug(Utils.objectToPrettyPrintedJson(body));
      } catch (JsonProcessingException e) {
         logger.error("Error processing JSON request message");
      }

      if (!MockServerDb.add(body)) {
         asyncResponse.resume(ErrorDetailFactory.getNotUniqueUuidErrorDetail(body.getId()));
         return;
      }

      // lookup meter

      // build response
   }

}
