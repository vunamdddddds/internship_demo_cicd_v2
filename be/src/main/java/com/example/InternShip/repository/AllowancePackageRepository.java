package com.example.InternShip.repository;

import com.example.InternShip.entity.AllowancePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowancePackageRepository extends JpaRepository<AllowancePackage, Integer>, JpaSpecificationExecutor<AllowancePackage> {
}
