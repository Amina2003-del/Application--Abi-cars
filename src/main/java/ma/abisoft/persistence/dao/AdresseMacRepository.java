package ma.abisoft.persistence.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import ma.abisoft.persistence.model.AdresseMac;

import java.util.List;

public interface AdresseMacRepository extends JpaRepository<AdresseMac, Long> {
    List<AdresseMac> findByUserId(Long userId);
}

