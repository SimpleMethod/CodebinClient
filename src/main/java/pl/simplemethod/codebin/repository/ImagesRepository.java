package pl.simplemethod.codebin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.simplemethod.codebin.model.Images;

import java.util.List;


@Repository
@Transactional
public interface ImagesRepository extends JpaRepository<Images, Long> {

Images getFirstById(Integer id);

Images getFirstByType(String type);

List<Images> getById(Integer id);

List<Images> getByName(String name);

Images getFirstByName(String name);

List<Images> getByType(String type);


}
