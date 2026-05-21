package com.example.demo.service;

import com.example.demo.entity.Wishlist;
import com.example.demo.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlishService {
    
    private final WishlistRepository wishlistRepository;

    // Thêm khách sạn vào wishlist
    public Wishlist addWishlist(Long userId, Long hotelId) {

        boolean exists = wishlistRepository
                .existsByUserIdAndHotelId(userId, hotelId);

        if (exists) {
            throw new RuntimeException("Hotel already exists in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setHotelId(hotelId);

        return wishlistRepository.save(wishlist);
    }

    // Lấy danh sách wishlist theo user
    public List<Wishlist> getWishlistByUser(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    // Xóa khỏi wishlist
    public void removeWishlist(Long userId, Long hotelId) {
        wishlistRepository.deleteByUserIdAndHotelId(userId, hotelId);
    }

    // Kiểm tra tồn tại
    public boolean isFavorite(Long userId, Long hotelId) {
        return wishlistRepository
                .existsByUserIdAndHotelId(userId, hotelId);
    }
}
