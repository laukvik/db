/*
 * Copyright 2015 Laukviks Bedrifter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laukvik.db.csv.jdbc;

import org.laukvik.db.jdbc.TextDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class TextDriverTest {

    @Test
    public void shouldConnect() throws SQLException {
        DriverManager.registerDriver(new TextDriver());
        try (
                Connection connection = DriverManager.getConnection("jdbc:TextDriver:/");
                Statement st = connection.createStatement()) {

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
