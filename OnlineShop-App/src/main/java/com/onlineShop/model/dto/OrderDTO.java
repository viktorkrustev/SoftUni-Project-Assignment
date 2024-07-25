package com.onlineshop.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OrderDTO {

    @NotBlank(message = "Address is required!")
    @Size(min = 3, max = 20, message = "Address must be at least 3 symbols!")
    private String deliveryAddress;

    @NotBlank(message = "Phone number is required!")
    @Pattern(regexp = "\\+?[0-9]+", message = "Phone number must contain only digits!")
    @Size(min = 10, max = 10, message = "Phone number must be 10 numbers!")
    private String contactPhone;

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
