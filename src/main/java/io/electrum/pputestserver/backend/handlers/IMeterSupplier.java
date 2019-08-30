package io.electrum.pputestserver.backend.handlers;

import io.electrum.prepaidutility.model.Meter;

public interface IMeterSupplier<T> {
   Meter getMeter(T request);
}
