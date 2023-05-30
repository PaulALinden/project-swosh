package org.example.regex;

public class RegEx {

    public boolean RegExIdentityNumber(String identityNumber) {

        String trimmedId = identityNumber.replaceAll("-", "");

        String regexId = "^[0-9]{12}+$";

        return trimmedId.matches(regexId);
    }

    public boolean RegExLetters(String name){
        String regex = "^[A-Za-z]+$";

        return name.matches(regex);
    }

    public boolean RegExNumbersLong(long numbersOnly){
        String regexAccount = "^[0-9]+$";

        return String.valueOf(numbersOnly).matches(regexAccount);
    }

    public boolean RegExNumbersDouble(double numbersOnly){
        String regexDouble = "^[0-9]+(\\.[0-9]+)?$";

        return String.valueOf(numbersOnly).matches(regexDouble);
    }
}
