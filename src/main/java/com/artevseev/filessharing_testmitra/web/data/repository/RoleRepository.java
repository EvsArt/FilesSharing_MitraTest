package com.artevseev.filessharing_testmitra.web.data.repository;

import com.artevseev.filessharing_testmitra.web.data.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findRoleByName(String name);
    boolean existsByName(String name);
}
