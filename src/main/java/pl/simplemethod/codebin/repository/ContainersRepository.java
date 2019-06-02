package pl.simplemethod.codebin.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.simplemethod.codebin.model.Containers;

import java.util.List;


@Repository
@Transactional
public interface ContainersRepository extends JpaRepository<Containers, Long> {

    List<Containers> removeByIdDocker(String name);

    List<Containers> removeAllByIdDocker(String name);

    Containers getFirstById(Long id);

    Containers getFirstByIdDocker(String dockerId);

    Containers getFirstByShareUrl(String shareURL);

    Containers getFirstByName(String  name);

    List<Containers> getByHostPorts(Integer ports);

    List<Containers> getByIdDocker(String dockerId);


}
