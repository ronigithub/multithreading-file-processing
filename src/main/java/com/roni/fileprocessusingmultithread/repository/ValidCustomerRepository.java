package com.roni.fileprocessusingmultithread.repository;

import com.roni.fileprocessusingmultithread.model.ValidCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidCustomerRepository extends JpaRepository<ValidCustomer, Long> {
}
