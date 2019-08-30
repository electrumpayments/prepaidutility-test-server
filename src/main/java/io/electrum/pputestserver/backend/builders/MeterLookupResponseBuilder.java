package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.MeterLookupResponse;

import javax.ws.rs.core.Response;

public class MeterLookupResponseBuilder implements IResponseBuilder<MeterLookupRequest> {

   @Override
   public Response getResponsePayload(MeterLookupRequest requestBody) throws UnknownMeterException {
      /*
       * Lookup meter
       */
      Meter meter = requestBody.getMeter();
      MeterLookupResponse responseBody = MockResponseTemplates.getMeterLookupResponse(meter.getMeterId());

      /*
       * Build and send error response if no match is found
       */
      if (responseBody == null) {
         throw new UnknownMeterException(requestBody.getMeter(), Response.Status.BAD_REQUEST);
      }

      /*
       * Build and send positive response
       */
      Utils.copyBaseFieldsFromRequest(responseBody, requestBody);
      Utils.logMessageTrace(responseBody);
      return Response.status(Response.Status.CREATED).entity(responseBody).build();
   }
}
