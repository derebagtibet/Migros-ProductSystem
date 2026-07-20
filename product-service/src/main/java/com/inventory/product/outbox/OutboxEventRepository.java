package com.inventory.product.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop20ByStatusInAndAttemptsLessThanOrderByCreatedAtAsc(
            Collection<OutboxEventStatus> statuses,
            int maxAttempts
    );
}
