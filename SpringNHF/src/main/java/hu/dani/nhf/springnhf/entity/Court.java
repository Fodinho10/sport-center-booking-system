package hu.dani.nhf.springnhf.entity;

import hu.dani.nhf.springnhf.enums.SportType;
import hu.dani.nhf.springnhf.enums.CourtStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(nullable = false)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CourtStatus courtStatus;
}
