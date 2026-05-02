package com.example.demo.repository;

import com.example.demo.Util.QueryUtil;
import com.example.demo.dto.Hotel.request.HotelFilter;
import com.example.demo.dto.Hotel.response.HotelFilterResponse;
import com.example.demo.entity.HotelAddress;
import com.example.demo.entity.Image;
import com.example.demo.enums.ImageEmun.RefType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Repository
@RequiredArgsConstructor
public class FilterRepositoryImpl implements FilterRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final HotelAddressRepository hotelAddressRepository;
    private final HotelAmenitiesRepository hotelAmenitiesRepository;
    private final ImageRepository imageRepository;

    @Override
    public Page<HotelFilterResponse> filterHotel(Pageable pageable, HotelFilter request) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT h.* FROM hotel h ");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        conditions.add("h.status = 2");

        if (StringUtils.isNotBlank(request.getName())) {
            conditions.add("h.name LIKE :name");
            params.put("name", "%" + request.getName() + "%");
        }
        if (request.getStar() != null) {
            conditions.add("h.star >= :star");
            params.put("star", request.getStar());
        }
        if (request.getAvgRating() != null) {
            conditions.add("h.rating_avg >= :ratingAvg");
            params.put("ratingAvg", request.getAvgRating());
        }

        if (StringUtils.isNotBlank(request.getCity())) {
            sql.append(" JOIN hotel_address ha ON h.id = ha.hotel_id ");
            conditions.add("ha.city LIKE :city");
            params.put("city", "%" + request.getCity() + "%");
        }

        boolean joinRoom = request.getMinPrice() != null || request.getMaxPrice() != null 
                           || StringUtils.isNotBlank(request.getRoomType()) 
                           || (request.getCheckInDate() != null && request.getCheckOutDate() != null);
        if (joinRoom) {
            sql.append(" JOIN room r ON h.id = r.hotel_id ");
            if (Objects.nonNull(request.getMinPrice()) && Objects.nonNull(request.getMaxPrice())) {
                conditions.add("r.pricepernight BETWEEN :minPrice AND :maxPrice");
                params.put("minPrice", request.getMinPrice());
                params.put("maxPrice", request.getMaxPrice());
            }
            if (StringUtils.isNotBlank(request.getRoomType())) {
                sql.append(" JOIN room_type rt ON r.room_type_id = rt.id ");
                conditions.add("rt.name LIKE :roomType");
                params.put("roomType", "%" + request.getRoomType() + "%");
            }
            if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
                sql.append(" JOIN room_availabilities ra ON r.id = ra.room_id ");
                conditions.add("ra.date BETWEEN :checkInDate AND :checkOutDate");
                conditions.add("ra.quantity_available > 0");
                params.put("checkInDate", request.getCheckInDate());
                params.put("checkOutDate", request.getCheckOutDate());
            }
        }

        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            sql.append(" JOIN hotel_amenities hame ON h.id = hame.hotel_id ");
            sql.append(" JOIN amenities a ON hame.amenity_id = a.id ");
            conditions.add("a.name IN (:amenities)");
            params.put("amenities", request.getAmenities());
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Handle Order By
        String orderBy = " ORDER BY h.id ASC";
        if (StringUtils.isNotBlank(request.getSort())) {
            try {
                String sortField = QueryUtil.checkSearchHotel(request.getSort());
                String orderDir = StringUtils.isNotBlank(request.getOrder()) ? request.getOrder() : "ASC";
                if (sortField.equals("price")) {
                    if (!joinRoom) {
                        sql.append(" JOIN room r_sort ON h.id = r_sort.hotel_id ");
                    }
                    orderBy = " ORDER BY " + (joinRoom ? "r.pricepernight" : "r_sort.pricepernight") + " " + orderDir;
                } else if (sortField.equals("avgRating")) {
                    orderBy = " ORDER BY h.rating_avg " + orderDir;
                } else if (sortField.equals("name")) {
                    orderBy = " ORDER BY h.name " + orderDir;
                } else if (sortField.equals("star")) {
                    orderBy = " ORDER BY h.star " + orderDir;
                }
            } catch (Exception e) {
                orderBy = " ORDER BY h.id ASC";
            }
        }
        
        String countQuery = "SELECT COUNT(DISTINCT h.id) FROM (" + sql.toString() + ") AS count_tbl";
        Integer total = jdbcTemplate.queryForObject(countQuery, params, Integer.class);
        if (total == null) total = 0;

        sql.append(orderBy);
        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getOffset());

        List<HotelFilterResponse> content = jdbcTemplate.query(
            sql.toString(),
            params,
            (rs, rowNum) -> {
                Long hotelId = rs.getLong("id");
                
                HotelAddress hotelAddr = hotelAddressRepository.findByHotelId(hotelId).orElse(null);
                String address = null;
                String district = null;
                String city = null;
                String country = null;
                
                if (hotelAddr != null) {
                    district = hotelAddr.getDistrict();
                    city = hotelAddr.getCity();
                    country = hotelAddr.getCountry();
                    address = district + ", " + city + ", " + country;
                }

                List<String> images = imageRepository.findByRefIdAndRefType(hotelId, RefType.HOTEL)
                        .stream()
                        .map(Image::getImageUrl)
                        .collect(Collectors.toList());



                List<String> amenities = hotelAmenitiesRepository.findAmenityNamesByHotelId(hotelId);

                return HotelFilterResponse.builder()
                    .id(hotelId)
                    .name(rs.getString("name"))
                    .star(rs.getInt("star"))
                    .rating_avg(rs.getFloat("rating_avg"))
                    .status(rs.getInt("status"))
                    .description(rs.getString("description"))
                    .address(address)
                    .district(district)
                    .city(city)
                    .country(country)
                    .images(images)
                    .amenities(amenities)
                    .checkin_time_start(rs.getTime("checkin_time_start"))
                    .checkin_time_end(rs.getTime("checkin_time_end"))
                    .checkout_time_start(rs.getTime("checkout_time_start"))
                    .checkout_time_end(rs.getTime("checkout_time_end"))
                    .created_at(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                    .updated_at(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                    .build();
            }
        );

        return new PageImpl<>(content, pageable, total);
    }
}
