package com.example.moviebooking.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.moviebooking.dto.ShowRequest;
import com.example.moviebooking.model.Show;
import com.example.moviebooking.repository.ShowRepository;

@Service
public class ShowService {
    private final ShowRepository showRepository;
    private final MongoTemplate mongoTemplate;

    public ShowService(ShowRepository showRepository, MongoTemplate mongoTemplate) {
        this.showRepository = showRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable(cacheNames = "shows:all")
    public List<Show> listShows() {
        return showRepository.findAll().stream()
                .map(this::normalizeShow)
                .toList();
    }

        @Cacheable(cacheNames = "showsByMovie", key = "#root.args[0]", unless = "#result == null || #result.isEmpty()")
    public List<Show> listShowsForMovie(String movieId) {
        return showRepository.findByMovieId(movieId).stream()
                .map(this::normalizeShow)
                .toList();
    }

        @Cacheable(cacheNames = "shows", key = "#root.args[0]", unless = "#result == null")
    public Show getShow(String showId) {
        return showRepository.findById(showId)
                .map(this::normalizeShow)
                .orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "shows:all", allEntries = true),
            @CacheEvict(cacheNames = "showsByMovie", key = "#root.args[0].movieId")
    })
    public Show createShow(ShowRequest request) {
        int capacity = request.getAvailableSeats();
        if (capacity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat capacity must be greater than zero");
        }
        Show show = new Show(request.getMovieId(), request.getShowTime(), capacity);
        show.setSeatLayout(buildSeatLayout(capacity));
        show.setReservedSeats(new HashSet<>());
        show.setAvailableSeats(capacity);
        Show saved = showRepository.save(show);
        return normalizeShow(saved);
    }

    @Caching(
            put = @CachePut(cacheNames = "shows", key = "#root.args[0]"),
            evict = {
                    @CacheEvict(cacheNames = "shows:all", allEntries = true),
                    @CacheEvict(cacheNames = "showsByMovie", key = "#result.movieId")
            }
    )
    public Show reserveSeats(String showId, List<String> requestedSeats) {
        if (requestedSeats == null || requestedSeats.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose at least one seat to continue");
        }

        Show show = getShow(showId);
        if (show == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found");
        }

        Set<String> normalizedSeats = requestedSeats.stream()
                .map(seat -> seat == null ? "" : seat.trim().toUpperCase(Locale.ROOT))
                .filter(seat -> !seat.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));

        if (normalizedSeats.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose at least one seat to continue");
        }

        Set<String> layout = new HashSet<>(show.getSeatLayout() != null ? show.getSeatLayout() : List.of());
        if (!layout.containsAll(normalizedSeats)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more seats are unavailable for this show");
        }

        Set<String> alreadyReserved = show.getReservedSeats() != null ? show.getReservedSeats() : new HashSet<>();
        for (String seat : normalizedSeats) {
            if (alreadyReserved.contains(seat)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat " + seat + " is already reserved");
            }
        }

        if (show.getAvailableSeats() < normalizedSeats.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough seats remaining for this selection");
        }

        Query query = Query.query(Criteria.where("_id").is(showId).and("reservedSeats").nin(normalizedSeats));
        Update update = new Update()
                .addToSet("reservedSeats").each(normalizedSeats.toArray())
                .inc("availableSeats", -normalizedSeats.size());
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Show updatedShow = mongoTemplate.findAndModify(query, update, options, Show.class);
        if (updatedShow == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected seats are no longer available");
        }
        return normalizeShow(updatedShow);
    }

    private List<String> buildSeatLayout(int capacity) {
        int seatsPerRow = 10;
        List<String> layout = new ArrayList<>(capacity);
        for (int index = 0; index < capacity; index++) {
            int rowIndex = index / seatsPerRow;
            int seatNumber = (index % seatsPerRow) + 1;
            String seatCode = rowLabel(rowIndex) + seatNumber;
            layout.add(seatCode.toUpperCase(Locale.ROOT));
        }
        return layout;
    }

    private String rowLabel(int index) {
        StringBuilder builder = new StringBuilder();
        int value = index;
        while (value >= 0) {
            int remainder = value % 26;
            builder.insert(0, (char) ('A' + remainder));
            value = (value / 26) - 1;
        }
        return builder.toString();
    }

    private Show normalizeShow(Show show) {
        if (show == null) {
            return null;
        }
        if (show.getSeatLayout() == null || show.getSeatLayout().isEmpty()) {
            int capacity = show.getCapacity() > 0 ? show.getCapacity() : Math.max(show.getAvailableSeats(), 0);
            show.setSeatLayout(buildSeatLayout(Math.max(capacity, 0)));
            show.setCapacity(capacity);
            if (show.getReservedSeats() == null) {
                show.setReservedSeats(new HashSet<>());
            }
            int reservedCount = show.getReservedSeats().size();
            int available = Math.max(capacity - reservedCount, 0);
            show.setAvailableSeats(available);
        } else {
            show.setSeatLayout(new ArrayList<>(show.getSeatLayout()));
        }
        if (show.getReservedSeats() == null) {
            show.setReservedSeats(new HashSet<>());
        } else {
            show.setReservedSeats(new HashSet<>(show.getReservedSeats()));
        }
        if (show.getCapacity() <= 0) {
            show.setCapacity(Math.max(show.getSeatLayout().size(), show.getAvailableSeats()));
        }
        if (show.getAvailableSeats() < 0) {
            show.setAvailableSeats(Math.max(show.getCapacity() - show.getReservedSeats().size(), 0));
        }
        return show;
    }
}
