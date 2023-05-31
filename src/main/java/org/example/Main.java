package org.example;

import org.example.database.InitDatabase;
import org.example.model.AccountModel;
import org.example.model.TransactionModel;
import org.example.view.MainMenu;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws SQLException {

        InitDatabase.getInstance();
        InitDatabase.initTables();

        MainMenu.mainMenu();
    }
}