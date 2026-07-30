package com.ordertracking.user.service.location;

import com.ordertracking.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenStreetMapLocationValidationService
        implements LocationValidationService {

    @Override
    public AddressValidationResponse validate(AddressValidationRequest request) {

        throw new UnsupportedOperationException(
                "OpenStreetMap validation is under implementation."
        );

    }

}