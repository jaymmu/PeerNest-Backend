
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.PasswordResetToken;
import com.PeerNest.PeerNest.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);
}
