package org.example.controller;

import org.example.model.UserModel;

import java.sql.SQLException;

public class NewUserController {

    public boolean newUser(String name, String identityNumber, String password, long account, long balance){

        String trimmedId = identityNumber.replaceAll("-", "");
        long parsedIdentityNumber = 0;

        try {
            parsedIdentityNumber = Long.parseLong(trimmedId);
        }catch(NumberFormatException e){
            System.out.println("---ERROR---");
            System.out.println("Use only numbers for identity");
            System.out.println("------------");

            return false;
        }

        //------Input filter----------
        String regexAccount = "^[0-9]+$";
        String regexId = "^[0-9]{12}+$";
        String regex = "^[A-Za-z]+$";

        if (name.matches(regex) && String.valueOf(parsedIdentityNumber).matches(regexId) && String.valueOf(account).matches(regexAccount) && String.valueOf(balance).matches(regexAccount)) {
            System.out.println("Valid input");
            UserModel userModel = new UserModel();

            userModel.createUser(name,parsedIdentityNumber,password, account, balance);

        } else {
            System.out.println("---ERROR---");
            System.out.println("Invalid input");
            System.out.println("------------");
            return false;
        }

        return true;
    }

    public boolean loginController(String identity, String password) throws SQLException {

        String trimmedId = identity.replaceAll("-", "");
        long parsedIdentityNumber = 0;

        try {
            parsedIdentityNumber = Long.parseLong(trimmedId);
        }catch(NumberFormatException e){
            System.out.println("---ERROR---");
            System.out.println("Use only numbers for identity");
            System.out.println("------------");

            return false;
        }

        //------Input filter----------
        String regexId = "^[0-9]{12}+$";

        if (String.valueOf(parsedIdentityNumber).matches(regexId)) {

            UserModel userModel = new UserModel();
            return userModel.verifyLogin(parsedIdentityNumber, password);

        } else {
            System.out.println("---ERROR---");
            System.out.println("Invalid input");
            System.out.println("------------");
            return false;
        }
    }
}
