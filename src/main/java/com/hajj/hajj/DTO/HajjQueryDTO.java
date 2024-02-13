package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HajjQueryDTO {
    String status;
    String fromDate;
    String toDate;
    String branchName;
    String isFromMobile;
    String isFundTransfered;
    String isPaid;
}
