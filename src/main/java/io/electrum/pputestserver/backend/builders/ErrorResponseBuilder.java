package io.electrum.pputestserver.backend.builders;

import io.electrum.pputestserver.backend.exceptions.IMeterException;
import io.electrum.prepaidutility.model.Meter;

import javax.ws.rs.core.Response;

public interface ErrorResponseBuilder<T> {
   Response getErrorResponse(T requestBody, Meter meter);

   Response getErrorResponseFromException(T requestBody, IMeterException meterException);
}
