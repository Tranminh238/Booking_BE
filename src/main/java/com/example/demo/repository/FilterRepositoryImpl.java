package com.example.demo.repository;

import com.example.demo.Util.QueryUtil;
import com.example.demo.dto.Hotel.request.HotelFilter;
import com.example.demo.dto.Hotel.response.HotelFilterResponse;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Repository
@RequiredArgsConstructor
public class FilterRepositoryImpl implements FilterRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String createGetHotelQuery(HotelFilter request) {
        String select = """
                select  h.id                                as hotelId,
                        h.name                              as hotelName,
                        h.star                              as hotelStar,
                        h.status                            as hotelStatus,
                        ha2.city                            as hotelCity,
                        h.rating_avg                        as hotelRatingAvg,
                        rt.name                             as roomTypeName,
                        MIN(r.price_per_night)              as roomPricePerNight,
                        GROUP_CONCAT(DISTINCT a.name)       as hotelAmenities

                from    Hotel h
                        join rooms r on h.id = r.hotel_id
                        join room_types rt on r.room_type_id = rt.id
                        join hotel_amenities ha on h.id = ha.hotel_id
                        join amenities a on ha.amenity_id = a.id
                        join hotel_addresses ha2 on h.id = ha2.hotel_id
                        left join room_availabilities ra2 on r.id = ra2.room_id
                """;
        List<String> whereList = new ArrayList<>();

        whereList.add("h.status = 2");
        boolean hasName = StringUtils.isNotBlank(request.getName());
        boolean hasCity = StringUtils.isNotBlank(request.getCity());
        if (hasName && hasCity && request.getName().equals(request.getCity())) {
            whereList.add("(h.name like :name OR ha2.city like :city)");
        } else {
            if (StringUtils.isNotBlank(request.getName())) {
                whereList.add("h.name like :name");
            }
            if (StringUtils.isNotBlank(request.getCity())) {
                whereList.add("(ha2.city like :city OR ha2.city like :cityNFD)");
            }
        }

        if (Objects.nonNull(request.getMinPrice()) && Objects.nonNull(request.getMaxPrice())) {
            whereList.add("r.price_per_night >= :minPrice AND r.price_per_night <= :maxPrice");
        }

        if (request.getStar() != null) {
            whereList.add("h.star >= :star");
        }
        if (request.getAvgRating() != null) {
            whereList.add("h.rating_avg >= :ratingAvg");
        }

        if (request.getNum_room() != null) {
            whereList.add("ra2.quantity_available >= :num_room");
        }
        if (request.getNum_guest() != null) {
            whereList.add("r.capacity >= :num_guest");
        }

        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            whereList.add("a.id IN (:amenityIds)");
        }

        if (StringUtils.isNotBlank(request.getRoomType())) {
            whereList.add("rt.name IN (:roomTypeIds)");
        }
        if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
            whereList.add("ra2.date BETWEEN :checkInDate AND :checkOutDate");
        }
        String where = "";
        if (!whereList.isEmpty()) {
            where = QueryUtil.createWhereQuery(whereList);
        }

        String groupBy = " GROUP BY h.id, h.name, h.star, h.status, ha2.city, h.rating_avg, rt.name";

        String order = " ORDER BY ";
        if (StringUtils.isNotBlank(request.getSort())) {
            try {
                order += QueryUtil.checkSearchHotel(request.getSort());
                if (StringUtils.isNotBlank(request.getOrder())) {
                    order += " " + request.getOrder();
                }
            } catch (Exception e) {
                order += "h.id ASC";
            }
        } else {
            order += "h.id ASC";
        }

        String query = select + where + groupBy + order;
        return query;
    }

    private Map<String, Object> createParams(HotelFilter request) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(request.getName())) {
            params.put("name", "%" + request.getName() + "%");
        }
        if (StringUtils.isNotBlank(request.getCity())) {
            params.put("city", "%" + request.getCity() + "%");
        }
        if (request.getStar() != null) {
            params.put("star", request.getStar());
        }
        if (request.getAvgRating() != null) {
            params.put("ratingAvg", request.getAvgRating());
        }
        if (Objects.nonNull(request.getMinPrice()) && Objects.nonNull(request.getMaxPrice())) {
            params.put("minPrice", request.getMinPrice());
            params.put("maxPrice", request.getMaxPrice());
        }
        if (request.getNum_room() != null) {
            params.put("num_room", request.getNum_room());
        }
        if (request.getNum_guest() != null) {
            params.put("num_guest", request.getNum_guest());
        }
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            params.put("amenityIds", request.getAmenities());
        }
        if (StringUtils.isNotBlank(request.getRoomType())) {
            params.put("roomTypeIds", java.util.Arrays.asList(request.getRoomType().split(",")));
        }
        if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
            params.put("checkInDate", request.getCheckInDate());
            params.put("checkOutDate", request.getCheckOutDate());
        }
        return params;
    }

    public Page<HotelFilterResponse> filterHotel(Pageable pageable, HotelFilter request) {
        List<HotelFilterResponse> hotelFilterResponses = jdbcTemplate.query(
                createGetHotelQuery(request),
                createParams(request),
                (rs, rowNum) -> HotelFilterResponse.builder()
                        .id(rs.getLong("hotelId"))
                        .name(rs.getString("hotelName"))
                        .star(rs.getInt("hotelStar"))
                        .status(rs.getInt("hotelStatus"))
                        .amenities(rs.getString("hotelAmenities") != null
                                ? java.util.Arrays.asList(rs.getString("hotelAmenities").split(","))
                                : new ArrayList<>())
                        .city(rs.getString("hotelCity"))
                        .rating_avg(rs.getFloat("hotelRatingAvg"))
                        .roomTypeName(rs.getString("roomTypeName"))
                        .roomPricePerNight(rs.getDouble("roomPricePerNight"))
                        .build());

        return new PageImpl<>(hotelFilterResponses, pageable, hotelFilterResponses.size());
    }
}
