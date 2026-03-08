package com.catalog.product;

import com.catalog.common.BaseEntity;
import com.catalog.common.converter.JsonAttributeConverter;
import com.catalog.producer.Producer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producer_id", nullable = false)
    private Producer producer;

    @Lob
    @Column(name = "attributes", nullable = false)
    @Convert(converter = JsonAttributeConverter.class)
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
}
