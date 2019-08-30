package io.electrum.pputestserver.resources;

import io.electrum.pputestserver.backend.handlers.MeterLookupRequestHandler;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.api.IMeterLookupsResource;
import io.electrum.prepaidutility.api.MeterLookupsResource;
import io.electrum.prepaidutility.model.MeterLookupRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

@Path("/prepaidutility/v3")
public class MeterLookupsResourceImpl extends MeterLookupsResource implements IMeterLookupsResource {

   static MeterLookupsResourceImpl instance = null;

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
         MeterLookupRequest requestBody,
         SecurityContext securityContext,
         AsyncResponse asyncResponse,
         Request request,
         HttpServletRequest httpServletRequest,
         HttpHeaders httpHeaders,
         UriInfo uriInfo) {
      /*
       * Log incoming message trace
       */
      Utils.logMessageTrace(requestBody);
      new MeterLookupRequestHandler().handleRequest(
            requestBody,
            lookupId,
            securityContext,
            asyncResponse,
            request,
            httpServletRequest,
            httpHeaders,
            uriInfo);
   }
}
