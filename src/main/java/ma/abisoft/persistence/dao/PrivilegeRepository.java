package ma.abisoft.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.abisoft.persistence.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);

}
