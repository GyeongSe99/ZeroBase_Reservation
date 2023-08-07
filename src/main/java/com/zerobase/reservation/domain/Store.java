package com.zerobase.reservation.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @ManyToOne
    private Member member;

    private String storeName;

    private String address;
    private double latitude;
    private double longitude;
    private String storeDescription;
    private boolean reservationAvailable;
    private double starRating;
    private Long reviewCount;
    private Long totalStar;


    @OneToMany(mappedBy = "store")
    private List<Review> reviews;

}
