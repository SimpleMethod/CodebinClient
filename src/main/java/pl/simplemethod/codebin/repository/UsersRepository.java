package pl.simplemethod.codebin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.simplemethod.codebin.model.Users;

@Repository
@Transactional
public interface UsersRepository  extends JpaRepository<Users, Long> {
    Users getFirstByGithub(Integer github);

    Users getFirstById(Integer id);

    Users getFirstByToken(String token);

    Users getFirstBySubscription(String subscription);

    @Query(value = "SELECT u FROM Users u INNER JOIN u.containers c WHERE c.idDocker = :id")
    Users findByContainersidDocker(@Param("id") String id);

    @Query(value = "SELECT u.subscription FROM Users u WHERE u.id = :id")
    String getSubscription(@Param("id") Integer id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET subscription = NULL WHERE subscription = :planId", nativeQuery = true)
    void updatePlan(@Param("planId") String planId);
}
