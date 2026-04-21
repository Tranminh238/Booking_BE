package com.example.demo.Util;

import com.example.demo.exception.hotelException;
import java.util.List;

public class QueryUtil {
    public static String checkSearchHotel(String hotel) throws hotelException {
        return switch (hotel) {
            case "price" -> "price";
            case "avgRating" -> "avgRating";
            case "name" -> "name";
            case "star" -> "star";
            case "totalReview" -> "totalReview";
            case "typeRoom" -> "typeRoom";
            default -> throw new hotelException("Invalid sort field");
        };
    }

    public static String createWhereQuery(List<String> whereList) {
        if (whereList == null || whereList.isEmpty()) {
            return "";
        }

        StringBuilder whereQuery = new StringBuilder("WHERE ");
        whereQuery.append(whereList.get(0));
        for (int i = 1; i < whereList.size(); i++) {
            whereQuery.append(" AND ").append(whereList.get(i));
        }
        return whereQuery.toString();
    }
    
}
