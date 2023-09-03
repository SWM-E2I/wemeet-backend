package com.e2i.wemeet.domain.cost;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Cost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long costId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer value;

    public Cost(Earn earn, Integer value) {
        this.type = earn.name();
        this.value = value;
    }

    public Cost(Spent spent) {
        this.type = spent.name();
        this.value = spent.getValue();
    }
}
