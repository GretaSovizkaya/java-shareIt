package item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import request.model.ItemRequest;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Item {
    Long id;
    Long ownerId;
    String name;
    String description;
    Boolean available;
    ItemRequest request;
}