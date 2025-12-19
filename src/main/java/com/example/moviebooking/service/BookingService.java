package com.example.moviebooking.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.moviebooking.dto.BookingRequest;
import com.example.moviebooking.model.Booking;
import com.example.moviebooking.model.Booking.PaymentSnapshot;
import com.example.moviebooking.model.Booking.UserSnapshot;
import com.example.moviebooking.model.Movie;
import com.example.moviebooking.model.Show;
import com.example.moviebooking.model.UserAccount;
import com.example.moviebooking.repository.BookingRepository;
import org.springframework.data.domain.Sort;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ShowService showService;
    private final MovieService movieService;
    private final AuthService authService;

    public BookingService(BookingRepository bookingRepository, ShowService showService, MovieService movieService, AuthService authService) {
        this.bookingRepository = bookingRepository;
        this.showService = showService;
        this.movieService = movieService;
        this.authService = authService;
    }

    public Booking createBooking(BookingRequest request) {
        Show show = showService.getShow(request.getShowId());
        if (show == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found");
        }

        Movie movie = movieService.getMovieById(show.getMovieId());
        if (movie == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found for show");
        }

        UserAccount userAccount = authService.requireUser(request.getUserId());

        List<String> normalizedSeats = normalizeSeats(request.getSeats());
        Show updatedShow = showService.reserveSeats(request.getShowId(), normalizedSeats);

        Booking booking = new Booking();
        booking.setUser(new UserSnapshot(userAccount.getUserId(), userAccount.getName(), userAccount.getRole()));
        booking.setShowId(show.getId());
        booking.setSeatsBooked(normalizedSeats.size());
        booking.setSeats(new ArrayList<>(normalizedSeats));
        booking.setTotalPrice(normalizedSeats.size() * 200.0);
        booking.setMovieTitle(movie.getTitle());
        booking.setShowTime(updatedShow.getShowTime());
        booking.setCreatedAt(Instant.now());

        PaymentSnapshot payment = new PaymentSnapshot(UUID.randomUUID().toString(), booking.getTotalPrice(), request.getPaymentMode(), "SUCCESS");
        booking.setPayment(payment);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Booking> getBookingsForUser(String userId) {
        return bookingRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    private List<String> normalizeSeats(List<String> requestedSeats) {
        if (requestedSeats == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose at least one seat to continue");
        }
        Set<String> normalized = requestedSeats.stream()
                .map(seat -> seat == null ? "" : seat.trim().toUpperCase(Locale.ROOT))
                .filter(seat -> !seat.isEmpty())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        if (normalized.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose at least one seat to continue");
        }
        return new ArrayList<>(normalized);
    }
}
