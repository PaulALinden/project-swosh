package org.example;

import org.example.database.InitDatabase;
import org.example.view.Menu;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        InitDatabase.getInstance();
        InitDatabase.initTables();

        Menu.mainMenu();
    }
}