package com.rj.ecommerce_backend.user.mappers;

import com.rj.ecommerce_backend.user.domain.Authority;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.dtos.*;
import com.rj.ecommerce_backend.user.repositories.UserRepository;
import com.rj.ecommerce_backend.user.valueobject.Address;
import com.rj.ecommerce_backend.user.valueobject.Email;
import com.rj.ecommerce_backend.user.valueobject.PhoneNumber;
import com.rj.ecommerce_backend.user.valueobject.ZipCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;

    public void updateBasicInformation(User user, UpdateBasicDetailsRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setAddress(new Address(
                request.address().street(),
                request.address().city(),
                new ZipCode(request.address().zipCode()),
                request.address().country()
        ));
        user.setPhoneNumber(new PhoneNumber(request.phoneNumber().value()));
        user.setActive(true);
        user.setDateOfBirth(request.dateOfBirth());
    }

    public void adminUpdateUserInformation(User user, AdminUpdateUserRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setAddress(new Address(
                request.address().street(),
                request.address().city(),
                new ZipCode(request.address().zipCode()),
                request.address().country()
        ));
        user.setPhoneNumber(new PhoneNumber(request.phoneNumber().value()));
        user.setDateOfBirth(request.dateOfBirth());
        user.setActive(request.isActive());
    }


    public UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().value(),
                mapToAddressDto(user.getAddress()),
                mapToPhoneNumberDto(user.getPhoneNumber().value()),
                user.getDateOfBirth(),
                user.getAuthorities().stream()
                        .map(Authority::getName)
                        .collect(Collectors.toSet()),
                user.isActive()
        );
    }


    public AddressDto mapToAddressDto(Address address) {
        return new AddressDto(
                address.street(),
                address.city(),
                address.zipCode().value(),
                address.country()
        );
    }

    public PhoneNumberDto mapToPhoneNumberDto(String phoneNumber) {
        return new PhoneNumberDto(
                phoneNumber
        );
    }

    public void validateNewEmail(String newEmail) {
        if (userRepository.existsByEmail(Email.of(newEmail))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }


}
