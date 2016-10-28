package com.istiak.blooddb.services;


import com.istiak.blooddb.dao.SpringDataDBUtils;
import com.istiak.blooddb.utils.GeoIPv4;
import com.istiak.blooddb.vo.LoginAudit;
import com.istiak.blooddb.vo.UserLoginVO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.List;

/**
 * Created by slbd on 10/28/16.
 */
@Service
public class LoginAuditService {
    public List<LoginAudit> loginAuditStatus() throws Exception {
        Query query = new Query();
        return SpringDataDBUtils.getMongoOperations().find(query, LoginAudit.class);
    }
    public int numberOfLogin(String username) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        int failureCount=0;
        String currentTime=LocalDateTime.now().toString();
        String searchStartTime=null;
        String[] separateTimeFromDatetime=currentTime.split("T");
        if (separateTimeFromDatetime==null){
            new Exception("Invalid Datetime");
        }
        searchStartTime=separateTimeFromDatetime[0]+"T00:00:00.000Z";
        Query query = new Query();
        query.addCriteria(
                Criteria.where("username").is(username)
                        .andOperator(
                                Criteria.where("time")
                                        .lte(LocalDateTime.parse(currentTime))
                                        .gte(LocalDateTime.parse(searchStartTime, formatter)))
        );
        query.limit(5);
        query.with(new Sort(Sort.Direction.DESC, "time"));
        List<LoginAudit> loginAuditing=SpringDataDBUtils.getMongoOperations().find(query, LoginAudit.class);
        for(LoginAudit loginAudit:loginAuditing)
        {
            //check 5 consecutive failed attempt
            if(!loginAudit.isStatus())
            {
                failureCount++;
            }else
            {
                //failureCount start from 0 if 5 consecutive failed not attempt ;
                failureCount=0;
            }
        }
        return failureCount;
    }

    public String lastLogin(String username) throws Exception {
        long minutes=0;
        long seconds=0;
        long hours=0;
        String currentTime=LocalDateTime.now().toString();
        Query query = new Query();
        query.addCriteria(
                Criteria.where("username").is(username)
                        .andOperator(
                                Criteria.where("time").lte(LocalDateTime.parse(currentTime)))
        );
        query.limit(1);
        query.with(new Sort(Sort.Direction.DESC,"time"));
        LoginAudit loginAuditing=SpringDataDBUtils.getMongoOperations().findOne(query, LoginAudit.class);
        try {
            //get last failure attempt time
            LocalDateTime fromDateTime = loginAuditing.getTime();
            // get system time
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime expireDateTime = LocalDateTime.from(fromDateTime);
            //add  default unlock time.currently default 24 hours
            expireDateTime = expireDateTime.plusHours(24);
            // Calculate remaining hours
            hours = expireDateTime.until(currentDateTime, ChronoUnit.HOURS);
            expireDateTime = expireDateTime.plusHours(hours);
            // Calculate remaining minutes
            minutes = expireDateTime.until(currentDateTime, ChronoUnit.MINUTES);
            expireDateTime = expireDateTime.plusMinutes(minutes);
            // Calculate remaining seconds
            seconds = expireDateTime.until(currentDateTime, ChronoUnit.SECONDS);
            //discard minus sign
            hours =Math.abs(hours);
            minutes=Math.abs(minutes);
            seconds=Math.abs(seconds);
        }catch (Exception e)
        {
            return  "error in Date";
        }
        return 	hours + " hours "  +minutes + " minutes " +seconds + " Seconds "   ;
    }

    public void loginAudit(HttpServletRequest requestContext, UserLoginVO userLogin, String message, boolean status) throws Exception
    {
        LoginAudit loginAudit = new LoginAudit();
        loginAudit.setUsername(userLogin.getEmail());
        loginAudit.setPassword(userLogin.getPassword());
        loginAudit.setStatus(status);
        loginAudit.setMessage(message);
        loginAudit.setIp(requestContext.getRemoteAddr());
        loginAudit.setTime(LocalDateTime.now());
        try {
            loginAudit.setLocation(GeoIPv4.getLocation(requestContext.getRemoteAddr()).toString());
        }catch(Exception e)
        {
            loginAudit.setLocation(null);
        }
        SpringDataDBUtils.getMongoOperations().insert(loginAudit);
    }

}
