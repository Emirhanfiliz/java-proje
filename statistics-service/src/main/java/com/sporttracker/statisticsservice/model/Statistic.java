package com.sporttracker.statisticsservice.model;

import com.sporttracker.shared.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistic implements BaseEntity<Long> {
    
    private Long id;
    private String userId;
    private String type;
    private Double value;
    private LocalDateTime calculationDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
