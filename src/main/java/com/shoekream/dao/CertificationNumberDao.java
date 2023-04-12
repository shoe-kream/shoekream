package com.shoekream.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

import static com.shoekream.common.util.constants.EmailConstants.LIMIT_TIME_CERTIFICATION_NUMBER;

@RequiredArgsConstructor
@Repository
public class CertificationNumberDao  {

    private final StringRedisTemplate stringRedisTemplate;

    public void saveCertificationNumber(String email, String certificationNumber) {
        stringRedisTemplate.opsForValue()
            .set(email, certificationNumber,
                Duration.ofSeconds(LIMIT_TIME_CERTIFICATION_NUMBER));
    }

    public String getCertificationNumber(String email) {
        return stringRedisTemplate.opsForValue().get(email);
    }

    public void removeCertificationNumber(String email) {
        stringRedisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(email);
    }
}

