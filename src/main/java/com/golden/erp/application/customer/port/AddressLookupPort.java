package com.golden.erp.application.customer.port;

import com.golden.erp.domain.customer.valueobject.Address;

public interface AddressLookupPort {

    Address lookup(String cep);
}
