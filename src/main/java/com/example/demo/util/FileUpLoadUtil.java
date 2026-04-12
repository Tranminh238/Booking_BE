package com.example.demo.Util;

import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import java.text.SimpleDateFormat;

@UtilityClass
public class FileUpLoadUtil {
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp|webp))$)";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String FILE_NAME_PATTERN = "%s_%s_%s";

    public static boolean isAllowedExtention(final String filename, final String pattern){
        final Matcher matcher = Pattern.compile(pattern).matcher(filename);
        return matcher.matches();
    }

    public static void assertAllowedExtention(MultipartFile file, final String pattern){
        final long size = file.getSize();
        if(size > MAX_FILE_SIZE){
            throw new IllegalArgumentException("Max file size is 2MB");
        }
        final String filename = file.getOriginalFilename();
        final String extension = FilenameUtils.getExtension(filename);
        if(!isAllowedExtention(filename, pattern)){
            throw new IllegalArgumentException("File type not allowed");
        }
    }

    public static String getFileName(final String name){
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FILE_NAME_PATTERN, FilenameUtils.getBaseName(name), date, FilenameUtils.getExtension(name));
    }


}
