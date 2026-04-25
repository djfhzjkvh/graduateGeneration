package com.graduation.crm.common.security;

import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import org.springframework.stereotype.Service;

/**
 * Central place for data-scope checks.
 * Security is disabled in the local demo profile, so these methods currently
 * keep the existing query behavior and can be tightened when auth is restored.
 */
@Service
public class DataScopeService {

    public void applyCustomerQueryScope(CustomerQueryDTO queryDTO) {
        // No-op for the local demo environment.
    }

    public void checkCustomerReadable(Long customerId) {
        // No-op for the local demo environment.
    }

    public void checkCustomerWritable(Long customerId) {
        // No-op for the local demo environment.
    }

    public Long currentUserIdOrDefault(Long userId) {
        return userId == null ? 1L : userId;
    }
}
