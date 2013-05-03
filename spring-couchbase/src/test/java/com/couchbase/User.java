package com.couchbase;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 테스트용 엔티티입니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 12. 5.
 */
@Slf4j
@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = 4195391816802258792L;

    private String firstName;
    private String lastName;
    private String addressStr;
    private String city;
    private String state;
    private String zipcode;
    private String email;
    private String username;
    private String password;

    private Double age;
    private Date updateTime;

    private byte[] byteArray = new byte[1024];

    private String Description;

    private Address homeAddress = new Address();
    private Address officeAddress = new Address();

    List<String> favoriteMovies = new ArrayList<String>();

    @Override
    public int hashCode() {
        return username.hashCode();
    }


    @Getter
    @Setter
    public static class Address implements Serializable {

        private static final long serialVersionUID = 5004748205792679032L;

        private String street;
        private String phone;

        private List<String> properties = new ArrayList<String>();
    }

    public static User getUser(int favoriteMovieSize) {

        User user = new User();
        user.setFirstName("성혁");
        user.setLastName("배");
        user.setAddressStr("정릉1동 현대홈타운 107동 301호");
        user.setCity("서울");
        user.setState("서울");
        user.setEmail("sunghyouk.bae@gmail.com");
        user.setUsername("debop");
        user.setPassword("debop");

        user.getHomeAddress().setPhone("999-9999");
        user.getHomeAddress().setStreet("정릉1동 현대홈타운 107동 301호");

        user.getOfficeAddress().setPhone("555-5555");
        user.getOfficeAddress().setStreet("동작동 삼성옴니타워 4층");


        for (int i = 0; i < favoriteMovieSize; i++)
            user.getFavoriteMovies().add("Favorite Movie Number-" + i);

        if (User.log.isDebugEnabled())
            User.log.debug("Create User...");

        return user;
    }
}

