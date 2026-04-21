// package com.example.demo.repository;

// import com.example.demo.Util.QueryUtil;
// import com.example.demo.dto.Hotel.request.HotelFilter;
// import com.example.demo.dto.Hotel.response.HotelResponse;

// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Objects;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.data.domain.Pageable;
// import java.util.Map;
// import java.util.HashMap;
// import org.springframework.data.domain.Page;
// import org.springframework.stereotype.Repository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

// @Repository
// @RequiredArgsConstructor
// public class FilterRepositoryImpl {
//     private final NamedParameterJdbcTemplate jdbcTemplate;
//     DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


//     private String createGetHotelQuery(HotelFilter request) {
//         String select = """
//                 select  h.id                    as hotelId
//                         h.name                  as hotelName
//                         h.star                  as hotelStar
//                         h.status                as hotelStatus
//                         h.amenityIds            as hotelAmenityIds
//                         h.city                  as hotelCity
//                         h.rating_avg            as hotelRatingAvg
//                         rt.name                 as roomTypeName
//                         r.pricepernight         as roomPricePerNight

//                 from    hotel h
//                         join room r on h.id = r.hotel_id
//                         join room_type rt on r.room_type_id = rt.id
//                         join hotel_amenity ha on h.id = ha.hotel_id
//                         join amenity a on ha.amenity_id = a.id
//                         join hotel_address ha on h.id = ha.hotel_id
//                         join room_amenity ra on r.id = ra.room_id
//                         join amenity a2 on ra.amenity_id = a2.id
//                         join room_availability ra2 on r.id = ra2.room_id
//                 """;
//         List<String> whereList = new ArrayList<>();
        
//         whereList.add("h.status = 2");
//         if(StringUtils.isNotBlank(request.getName())){
//             whereList.add("h.name like :name");
//         }
//         if (Objects.nonNull(request.getMinPrice()) && Objects.nonNull(request.getMaxPrice())) {
//             whereList.add("r.pricepernight >= :minPrice AND r.pricepernight <= :maxPrice");
//         }
//         if(StringUtils.isNotBlank(request.getCity())){
//             whereList.add("ha.city like :city");
//         }
//         if(request.getStar() != null){
//             whereList.add("h.star >= :star");
//         }
//         if(request.getAvgRating() != null){
//             whereList.add("h.rating_avg >= :ratingAvg");
//         }
//         if(request.getAmenities() != null){
//             whereList.add("h.amenityIds like :amenityIds");
//         }
//         if(StringUtils.isNotBlank(request.getRoomType())){
//             whereList.add("h.roomTypeIds like :roomTypeIds");
//         }
//         if(request.getCheckInDate() != null && request.getCheckOutDate() != null){
//             whereList.add("ra.date BETWEEN :checkInDate AND :checkOutDate");
//         }
//         String where = "";
//         if(!whereList.isEmpty(){
//             where = QueryUtil.createWhereQuery(whereList);
//         }

//         String order = "ORDER BY ";
//         if(StringUtils.isNotBlank(request.getSort())){
//             try{
//                 order += QueryUtil.checkSearchHotel(request.getSort());
//                 if(StringUtils.isNotBlank(request.getOrder())){
//                     order += " " + request.getOrder();
//                 }
//             }catch(Exception e){
//                 order += "id ASC";
//             }
//         }

//         String query = select + where + order;
//         return query;
//     }

//     private Map<String, Object> createParams(HotelFilter request){
//         Map<String, Object> params = new HashMap<>();
//         if(StringUtils.isNotBlank(request.getName())){
//             params.put("name", "%" + request.getName() + "%");
//         }
//         if(StringUtils.isNotBlank(request.getCity())){
//             params.put("city", "%" + request.getCity() + "%");
//         }
//         if(request.getStar() != null){
//             params.put("star", request.getStar());
//         }
//         if(request.getAvgRating() != null){
//             params.put("ratingAvg", request.getAvgRating());
//         }
//         if(request.getAmenities() != null){
//             params.put("amenityIds", "%" + request.getAmenities() + "%");
//         }
//         if(StringUtils.isNotBlank(request.getRoomType())){
//             params.put("roomTypeIds", "%" + request.getRoomType() + "%");
//         }
//         if(request.getCheckInDate() != null && request.getCheckOutDate() != null){
//             params.put("checkInDate", request.getCheckInDate());
//             params.put("checkOutDate", request.getCheckOutDate());
//         }
//         return params;
//     }

//     public Page<HotelResponse> filterHotel(HotelFilter request, Pageable pageable){
//         List<HotelResponse> hotelResponses = jdbcTemplate.query(
//             createGetHotelQuery(request), 
//             createParams(request),
//             (rs, rowNum) -> HotelResponse.builder()
//                 .id(rs.getLong("id"))
//                 .name(rs.getString("name"))
//                 .star(rs.getInt("star"))
//                 .status(rs.getInt("status"))
//                 .amenities(rs.getString("amenities"))
//                 .city(rs.getString("city"))
//                 .rating_avg(rs.getDouble("rating_avg"))
//                 .roomTypeName(rs.getString("roomTypeName"))
//                 .roomPricePerNight(rs.getDouble("roomPricePerNight"))
//                 .build()
//         );

//     }
// }
