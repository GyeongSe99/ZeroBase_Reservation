package com.zerobase.reservation.domain;

import com.zerobase.reservation.type.PartnershipStatus;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Member {

    @Id
    private String memberId;  // email

    private String memberName;

    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    // partner: 점주, general: 일반인
    private PartnershipStatus partnershipStatus;

    @OneToMany(mappedBy = "member")
    private List<Store> stores;

}
