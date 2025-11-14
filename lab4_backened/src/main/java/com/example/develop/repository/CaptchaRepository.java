package com.example.develop.repository;

import com.example.develop.entity.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

public interface CaptchaRepository extends JpaRepository<Captcha, String> {
    // 根据ID查找验证码
    Captcha findCaptchaById(String id);

    // 删除过期验证码
    @Modifying
    @Transactional
    @Query("DELETE FROM Captcha c WHERE c.expiresAt < ?1")
    void deleteExpiredCaptchas(LocalDateTime now);
}
