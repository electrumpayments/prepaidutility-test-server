package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;

import javax.ws.rs.core.Response;

public interface IResponseBuilder<T> {
   Response getResponsePayload(T requestBody) throws UnknownMeterException;
}
