package com.ordertracking.user.service.location;

import com.ordertracking.user.dto.AddAddressRequest;
import com.ordertracking.user.dto.AddressValidationRequest;
import com.ordertracking.user.dto.AddressValidationResponse;

public interface LocationValidationService {

    AddressValidationResponse validate(AddressValidationRequest request);

}