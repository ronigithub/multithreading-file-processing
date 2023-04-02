package com.roni.fileprocessusingmultithread.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * All valid customers
 */
@Entity
@Getter @Setter @NoArgsConstructor
@Accessors(chain = true)
public class ValidCustomer {
    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Long id;

    private String name;
    private String email;
    /**
     * It will be another table for normalization
     */
    private String addressString;
    private String mobile;
    private String ip;
}
