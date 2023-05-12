package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "friend_requests")
@Data
public class FriendRequest {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    public FriendRequest(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = FriendRequestStatus.PENDING;
    }

    public FriendRequest(Long senderId, Long receiverId, FriendRequestStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public FriendRequest(){}
}
