package com.domeni.kapita.kafka.inbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxEventSpringRepository extends JpaRepository<InboxEvent, String> {}
