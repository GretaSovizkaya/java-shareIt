package item.repository;

import item.model.Comment;
import item.model.Item;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Long> {
    List<Comment> findByItem(Item item);

    List<Comment> findByItemIn(List<Item> items, Sort created);
}