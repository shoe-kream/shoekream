package com.shoekream.domain.point;

import com.shoekream.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findAllByUserAndDivisionOrderByCreatedDateDesc(User user, PointDivision division);
}
