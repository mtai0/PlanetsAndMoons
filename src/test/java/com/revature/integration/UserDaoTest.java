package com.revature.integration;

import com.revature.MainDriverTest;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class UserDaoTest {
    private Connection connection;

    @BeforeEach
    public void setupTest() throws SQLException {
        connection = ConnectionUtil.createConnection();
    }

    @AfterEach
    public void cleanupTest() throws SQLException {
        if (connection != null) connection.close();
    }

    @Test
    public void helloWorld(){
    }
}
