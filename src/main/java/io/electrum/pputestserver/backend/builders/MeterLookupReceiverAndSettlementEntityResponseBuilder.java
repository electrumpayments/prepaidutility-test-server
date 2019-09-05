package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.MeterLookupRequest;
import io.electrum.prepaidutility.model.MeterLookupResponse;
import io.electrum.vas.model.Institution;

import javax.ws.rs.core.Response;

public class MeterLookupReceiverAndSettlementEntityResponseBuilder implements ResponseBuilder<MeterLookupRequest> {

   @Override
   public Response getResponsePayload(MeterLookupRequest requestBody) throws UnknownMeterException {
      MeterLookupResponse meterLookupResponse =
            MockResponseTemplates.getMeterLookupResponse(requestBody.getMeter().getMeterId());
      Utils.copyBaseFieldsFromRequest(meterLookupResponse, requestBody);

      meterLookupResponse.setReceiver(new Institution().id("63294000").name("TestReceiver"));
      meterLookupResponse.setSettlementEntity(new Institution().id("63294001").name("TestSettlementEntity"));

      return Response.status(Response.Status.CREATED).entity(meterLookupResponse).build();
   }
}
