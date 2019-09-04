package io.electrum.pputestserver.backend.handlers;

import io.electrum.pputestserver.backend.exceptions.UnknownMeterException;
import io.electrum.prepaidutility.model.Meter;

public abstract class BaseErrorRequestHandler<T> implements ErrorRequestHandler<T> {

   private final MeterSupplier<T> meterSupplier;

   public BaseErrorRequestHandler(MeterSupplier<T> meterSupplier) throws UnknownMeterException {
      this.meterSupplier = meterSupplier;
   }

   public Meter getMeter(T request) {
      return meterSupplier.getMeter(request);
   }
}
