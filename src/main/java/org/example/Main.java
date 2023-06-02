package org.example;

import org.example.database.InitDatabase;
import org.example.view.MainMenu;

public class Main {
    public static void main(String[] args) {

        InitDatabase.getInstance();
        InitDatabase.initTables();

        MainMenu.displayMainMenu();
    }
}