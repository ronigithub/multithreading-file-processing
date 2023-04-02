package com.roni.fileprocessusingmultithread.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Invalid customers
 */
@Entity
@Getter @Setter @NoArgsConstructor
@Accessors(chain = true)
public class InvalidCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
