package pl.simplemethod.codebin.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.simplemethod.codebin.model.Images;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class ImagesDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Images images) {
        entityManager.persist(images);
    }

    @Transactional()
    public Images get(Integer id) {
        Images images = entityManager.find(Images.class, id);
        return images;
    }

    @Transactional
    public void update(Integer id, Images images) {
        Images find = entityManager.find(Images.class, id);
        if (find != null) {
            find.setId(images.getId());
            find.setName(images.getName());
            find.setType(images.getType());
            find.setCreateTime(images.getCreateTime());
            find.setDocker_id(images.getDocker_id());
        }
    }

    @Transactional
    public void remove(Integer id) {
        Images objToRemove = entityManager.find(Images.class, id);
        entityManager.remove(objToRemove);
    }
}
