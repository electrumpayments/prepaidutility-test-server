package io.electrum.pputestserver.backend;

import io.electrum.pputestserver.backend.handlers.ErrorRequestHandler;
import io.electrum.pputestserver.backend.handlers.RequestHandler;

public interface RequestHandlerFactory<T> {

   public RequestHandler<T> getRequestHandler();

   public ErrorRequestHandler<T> getErrorRequestHandler();
}
