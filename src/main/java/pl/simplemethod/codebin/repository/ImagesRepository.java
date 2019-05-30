package pl.simplemethod.codebin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.simplemethod.codebin.model.Images;

import java.util.List;
@Repository
public interface ImagesRepository extends JpaRepository<Images, Integer> {

    @Query(value = "SELECT * FROM images u WHERE u.id_images = ?1", nativeQuery = true)
    List<Images> getById(Integer id);

    @Query(value = "SELECT * FROM images u WHERE u.name = ?1", nativeQuery = true)
    List<Images> getByName(String name);

    @Query(value = "SELECT * FROM images u WHERE u.type = ?1", nativeQuery = true)
    List<Images> getByType(String type);

    @Query(value = "SELECT * FROM images u WHERE u.docker_id = ?1", nativeQuery = true)
    List<Images> getbyDockerID(String type);

    @Query(value = "SELECT * FROM images u ", nativeQuery = true)
    List<Images> getAll();
}
