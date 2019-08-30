package io.electrum.pputestserver.backend.handlers;

import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.prepaidutility.model.Meter;

public abstract class AErrorRequestHandler<T> implements IErrorRequestHandler<T> {

   private final IMeterSupplier<T> meterSupplier;

   public AErrorRequestHandler(IMeterSupplier<T> meterSupplier) throws UnknownMeterException {
      this.meterSupplier = meterSupplier;
   }

   public Meter getMeter(T request) {
      return meterSupplier.getMeter(request);
   }
}
