package com.example.demo.service;

import com.example.demo.dto.Hotel.response.HotelResponse;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    @Transactional
    public boolean toggleWishlist(Long userId, Long hotelId) {
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndHotelId(userId, hotelId);
        if (existing.isPresent()) {
            wishlistRepository.deleteByUserIdAndHotelId(userId, hotelId);
            return false; // Removed from wishlist
        } else {
            Wishlist wishlist = Wishlist.builder()
                    .userId(userId)
                    .hotelId(hotelId)
                    .build();
            wishlistRepository.save(wishlist);
            return true; // Added to wishlist
        }
    }

    public List<HotelResponse> getWishlistHotels(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        List<Long> hotelIds = wishlists.stream().map(Wishlist::getHotelId).toList();
        List<Hotel> hotels = hotelRepository.findAllById(hotelIds);
        return hotels.stream().map(hotelService::toResponse).toList();
    }

    public List<Long> getWishlistHotelIds(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream().map(Wishlist::getHotelId).toList();
    }
}
