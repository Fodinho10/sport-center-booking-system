package hu.dani.nhf.springnhf.entity;

import hu.dani.nhf.springnhf.enums.EquipmentStatus;
import hu.dani.nhf.springnhf.enums.SportType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@NoArgsConstructor
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(nullable = false)
    private Integer totalInventory;

    @Column(nullable = false)
    private BigDecimal hourlyRate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus equipmentStatus;
}
