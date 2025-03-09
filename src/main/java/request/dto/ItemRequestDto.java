package request.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class ItemRequestDto {
    String description;
    long requestor;
    LocalDate created;
}