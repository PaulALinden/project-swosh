package org.example;

import org.example.database.InitDatabase;
import org.example.model.AccountModel;
import org.example.model.TransactionModel;
import org.example.view.MainMenu;

import javax.naming.CommunicationException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        InitDatabase.getInstance();
        InitDatabase.initTables();

        MainMenu.mainMenu();
    }
}