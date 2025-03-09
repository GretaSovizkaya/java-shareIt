package request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long requestorId);
    Optional<ItemRequest> findByIdOrderByCreatedAsc(Long itemRequestId);
}