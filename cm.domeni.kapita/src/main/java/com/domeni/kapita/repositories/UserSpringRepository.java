package com.domeni.kapita.repositories;

import com.domeni.kapita.domain.user.User;
import com.domeni.kapita.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpringRepository extends JpaRepository<User, UserId> {}
