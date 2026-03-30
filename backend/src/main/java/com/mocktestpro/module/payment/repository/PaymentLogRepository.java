package com.mocktestpro.module.payment.repository;

import com.mocktestpro.module.payment.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID> {

    List<PaymentLog> findByPurchaseIdOrderByCreatedAtAsc(UUID purchaseId);
}