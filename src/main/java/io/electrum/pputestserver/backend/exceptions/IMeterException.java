package io.electrum.pputestserver.backend.exceptions;

import io.electrum.prepaidutility.model.ErrorDetail;
import io.electrum.prepaidutility.model.Meter;

import javax.ws.rs.core.Response;

public interface IMeterException {

   Meter getMeter();

   Response buildErrorDetailResponse(String msgId, String originalMsgId, ErrorDetail.RequestType requestType, Meter meter);
}
