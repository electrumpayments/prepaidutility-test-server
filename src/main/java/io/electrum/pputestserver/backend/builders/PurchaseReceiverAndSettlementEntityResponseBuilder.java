package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.MockResponseTemplates;
import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.pputestserver.utils.Utils;
import io.electrum.prepaidutility.model.PurchaseRequest;
import io.electrum.prepaidutility.model.PurchaseResponse;
import io.electrum.prepaidutility.model.Utility;
import io.electrum.vas.model.Customer;
import io.electrum.vas.model.Institution;

import javax.ws.rs.core.Response;

public class PurchaseReceiverAndSettlementEntityResponseBuilder implements ResponseBuilder<PurchaseRequest> {

   @Override
   public Response getResponsePayload(PurchaseRequest requestBody) throws UnknownMeterException {
      PurchaseResponse purchaseResponse =
            MockResponseTemplates.getPartialPurchaseResponse(requestBody.getMeter().getMeterId());
      Utils.copyBaseFieldsFromRequest(purchaseResponse, requestBody);

      // All the money is going to be used for paying debt, so no tokens will be sent back.
      purchaseResponse.setMeter(requestBody.getMeter());
      purchaseResponse.setCustomer(new Customer().address("Wonderland").firstName("Alice").lastName("Can't remember!"));
      purchaseResponse.setUtility(
            new Utility().address("1 quantum street")
                  .clientId("6329412345")
                  .message("message")
                  .name("Quantum Elec")
                  .vatRegNum("9321654873521"));

      purchaseResponse.setReceiver(new Institution().id("63294000").name("TestReceiver"));
      purchaseResponse.setSettlementEntity(new Institution().id("63294001").name("TestSettlementEntity"));

      return Response.status(Response.Status.CREATED).entity(purchaseResponse).build();
   }
}
