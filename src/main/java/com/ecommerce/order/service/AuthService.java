package com.ecommerce.order.service;

import com.ecommerce.order.dto.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
