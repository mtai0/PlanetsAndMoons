package com.revature.integration;

import com.revature.MainDriverTest;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class UserDaoTest {
    private Connection connection;
    private UserDao dao;

    @BeforeEach
    public void setupTest(){
        dao = new UserDao();
        try {
            connection = ConnectionUtil.createConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM users");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void cleanupTest(){
        if (connection != null) {
            try {
                connection = ConnectionUtil.createConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM users");
                ps.executeUpdate();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        dao = null;
    }

    @Test
    public void createUserSuccess(){
        
    }
}
