package com.shoekream.service;

import com.shoekream.common.exception.ShoeKreamException;
import com.shoekream.domain.point.Point;
import com.shoekream.domain.point.PointDivision;
import com.shoekream.domain.point.PointRepository;
import com.shoekream.domain.point.dto.PointChargeRequest;
import com.shoekream.domain.point.dto.PointHistoryResponse;
import com.shoekream.domain.point.dto.PointResponse;
import com.shoekream.domain.point.dto.PointWithdrawalRequest;
import com.shoekream.domain.user.User;
import com.shoekream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.shoekream.common.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Transactional(readOnly = true)
    public Long getUserPoint(String email) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        return foundUser.getPoint();
    }

    public PointResponse chargePoint(String email, PointChargeRequest requestDto) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        foundUser.chargePoint(requestDto.getAmount());

        pointRepository.save(requestDto.toEntity(foundUser));

        return foundUser.toPointResponse();

    }

    public PointResponse withdrawalPoint(String email, PointWithdrawalRequest requestDto) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        foundUser.checkPassword(encoder,requestDto.getPassword());

        pointRepository.save(requestDto.toEntity(foundUser));

        foundUser.withdrawalPoint(requestDto.getWithdrawalAmount());

        return foundUser.toPointResponse();
    }

    public List<PointHistoryResponse> getHistoryPointByDivision(String email, PointDivision division) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ShoeKreamException(USER_NOT_FOUND));

        return pointRepository.findAllByUserAndDivisionOrderByCreatedDateDesc(foundUser, division)
                .stream().map(Point::toPointHistoryResponse)
                .collect(Collectors.toList());
    }
}
