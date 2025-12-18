package com.example.moviebooking.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private UserSnapshot user;
    private String showId;
    private int seatsBooked;
    private double totalPrice;
    private PaymentSnapshot payment;
    private String movieTitle;
    private String showTime;
    private Instant createdAt;
    private List<String> seats;

    public Booking() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserSnapshot getUser() {
        return user;
    }

    public void setUser(UserSnapshot user) {
        this.user = user;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentSnapshot getPayment() {
        return payment;
    }

    public void setPayment(PaymentSnapshot payment) {
        this.payment = payment;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public static class UserSnapshot {
        private String userId;
        private String name;
        private String role;

        public UserSnapshot() {
        }

        public UserSnapshot(String userId, String name, String role) {
            this.userId = userId;
            this.name = name;
            this.role = role;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class PaymentSnapshot {
        private String paymentId;
        private double amount;
        private String paymentMode;
        private String paymentStatus;

        public PaymentSnapshot() {
        }

        public PaymentSnapshot(String paymentId, double amount, String paymentMode, String paymentStatus) {
            this.paymentId = paymentId;
            this.amount = amount;
            this.paymentMode = paymentMode;
            this.paymentStatus = paymentStatus;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
            this.paymentMode = paymentMode;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }
}
