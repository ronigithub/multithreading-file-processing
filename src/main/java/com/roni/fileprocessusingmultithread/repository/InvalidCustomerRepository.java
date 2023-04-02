package com.roni.fileprocessusingmultithread.repository;

import com.roni.fileprocessusingmultithread.model.InvalidCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidCustomerRepository extends JpaRepository<InvalidCustomer, Long> {
}
