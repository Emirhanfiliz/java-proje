package com.sporttracker.statisticsservice.repository;

import com.sporttracker.statisticsservice.model.Statistic;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Statistic> rowMapper = (rs, rowNum) -> Statistic.builder()
            .id(rs.getLong("id"))
            .userId(rs.getString("user_id"))
            .type(rs.getString("type"))
            .value(rs.getDouble("value"))
            .calculationDate(rs.getTimestamp("calculation_date").toLocalDateTime())
            .build();

    public Statistic save(Statistic statistic) {
        String sql = "INSERT INTO statistics (user_id, type, value, calculation_date) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, statistic.getUserId());
            ps.setString(2, statistic.getType());
            ps.setDouble(3, statistic.getValue());
            ps.setTimestamp(4, Timestamp.valueOf(statistic.getCalculationDate()));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            statistic.setId(key.longValue());
        }
        return statistic;
    }

    public List<Statistic> findByUserId(String userId) {
        String sql = "SELECT * FROM statistics WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<Statistic> findAll() {
        String sql = "SELECT * FROM statistics";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
