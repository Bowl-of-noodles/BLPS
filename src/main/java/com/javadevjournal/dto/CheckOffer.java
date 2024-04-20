package com.javadevjournal.dto;

import lombok.Value;
import java.io.Serializable;

@Value
public class CheckOffer implements Serializable {
     Long id;
     Long adId;
     String status;
}
