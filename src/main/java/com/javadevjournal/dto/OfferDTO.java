package com.javadevjournal.dto;

import com.javadevjournal.jpa.enums.StatusName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {
    private Long id;
    private LocalDate creationDate;
    private Long customerId;
    private Long adId;
    private Long price;
    private StatusName status;

}
