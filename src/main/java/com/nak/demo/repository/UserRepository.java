package com.nak.demo.repository;



import com.nak.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE :name IS NULL OR LOWER(u.name) LIKE %:name% ")
    List<User> findUserWithFilters(@Param("name") String name);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);

    Optional<User> findByName(String usename);
    Optional<User> findByEmail(String email);

    boolean existsByNameIgnoreCase(String name);
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
