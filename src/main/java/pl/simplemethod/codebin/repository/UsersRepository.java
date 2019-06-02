package pl.simplemethod.codebin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.simplemethod.codebin.model.Users;

@Repository
@Transactional
public interface UsersRepository  extends JpaRepository<Users, Long> {
    Users getFirstByGithub(Integer github);

    Users getFirstById(Integer id);

    Users getFirstByToken(String token);

    Users getFirstBySubscription(String  subscription);

    Users findByContainersidDocker(String id);
}
