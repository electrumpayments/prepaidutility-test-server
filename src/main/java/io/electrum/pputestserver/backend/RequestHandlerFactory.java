package io.electrum.pputestserver.backend;

import io.electrum.pputestserver.backend.handlers.IErrorRequestHandler;
import io.electrum.pputestserver.backend.handlers.IRequestHandler;

public interface RequestHandlerFactory<T> {

   public IRequestHandler<T> getRequestHandler();

   public IErrorRequestHandler<T> getErrorRequestHandler();
}
