package com.skyconnect.demo.repository;

import com.skyconnect.demo.entity.User;
import com.skyconnect.demo.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Get all users with a particular role
    List<User> findByRole(Role role);

    // Search customers by name or email
    @Query("""
            SELECT u
            FROM User u
            WHERE u.role = :role
            AND (
                LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR
                LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    List<User> searchCustomers(
            @Param("role") Role role,
            @Param("keyword") String keyword
    );
}