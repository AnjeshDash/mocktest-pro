package com.mocktestpro.module.payment.repository;

import com.mocktestpro.module.payment.entity.PaymentStatus;
import com.mocktestpro.module.payment.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    boolean existsByAttendee_IdAndMockTest_Id(UUID attendeeId, UUID testId);

    boolean existsByAttendee_IdAndMockTest_IdAndPaymentStatus(
            UUID attendeeId, UUID testId, PaymentStatus status);

    Page<Purchase> findByAttendee_IdOrderByPurchasedAtDesc(UUID attendeeId, Pageable pageable);

    @Query("SELECT p FROM Purchase p JOIN FETCH p.mockTest WHERE p.id = :id")
    Optional<Purchase> findByIdWithTest(@Param("id") UUID id);

    Optional<Purchase> findByAttendee_IdAndMockTest_IdAndPaymentStatus(
            UUID attendeeId, UUID testId, PaymentStatus status);
}