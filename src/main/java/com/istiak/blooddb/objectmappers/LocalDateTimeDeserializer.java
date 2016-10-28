/**
 * Imtiaz Mirza imz.mrz@gmail.com
 * For BDreview Inc
 * 
 */
package com.istiak.blooddb.objectmappers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
	static final Logger logger = LoggerFactory.getLogger(LocalDateTimeDeserializer.class);
    @Override
    public LocalDateTime deserialize(JsonParser jp, 
            DeserializationContext dc) throws IOException {
        ObjectCodec codec = jp.getCodec();
        TextNode node = codec.readTree(jp);
        String dateString = node.textValue();
        Instant instant = Instant.parse(dateString);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    	logger.info("######DateTime##### "+dateTime);
        
        return dateTime;
    } 
}