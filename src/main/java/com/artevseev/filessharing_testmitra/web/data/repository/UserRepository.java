package com.artevseev.filessharing_testmitra.web.data.repository;

import com.artevseev.filessharing_testmitra.web.data.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
    List<User> findAll();
}
