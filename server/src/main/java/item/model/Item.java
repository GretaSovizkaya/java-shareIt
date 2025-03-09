package item.model;

import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.*;
import lombok.experimental.FieldDefaults;
import request.model.ItemRequest;
import user.model.User;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    User owner;
    @Column(name = "NAME", length = 200, nullable = false)
    String name;
    @Column(length = 100, nullable = false)
    String description;
    @BooleanFlag
    @Column(name = "available", nullable = false)
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest itemRequest;
}