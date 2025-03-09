package item.repository;

import item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import user.model.User;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(User user);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:text IS NOT NULL AND :text != '') " +
            "AND (i.available = TRUE)")
    List<Item> searchByNameOrDescription(@Param("text") String text);
}