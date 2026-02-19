package com.golden.erp.application.order.port;

import java.math.BigDecimal;

public interface ExchangeRatePort {

    BigDecimal getBrlToUsdRate();
}
