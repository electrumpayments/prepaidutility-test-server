package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.Meter;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.prepaidutility.model.PurchaseResponse;

import javax.ws.rs.core.Response;

public class PurchaseResponseBuilder implements IResponseBuilder<PurchaseRequest> {

   @Override
   public Response getResponsePayload(PurchaseRequest requestBody) throws UnknownMeterException {

      /*
       * Lookup response corresponding to this meter id
       */
      Meter meter = requestBody.getMeter();
      PurchaseResponse responseBody = MockResponseTemplates.getPurchaseResponse(meter.getMeterId());

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
