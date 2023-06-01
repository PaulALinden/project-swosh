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

    public boolean RegExNumbersLong(String numbersOnly){
        String regexAccount = "^[0-9]+$";

        return String.valueOf(numbersOnly).matches(regexAccount);
    }

    public boolean RegExNumbersDouble(String numbersOnly){
        String regexDouble = "^[0-9]+(\\.[0-9]+)?$";

        return String.valueOf(numbersOnly).matches(regexDouble);
    }

    public boolean RegExNumbersDate(String date){
        String regexPatternDate = "\\d{4}-\\d{2}-\\d{2}";

        return String.valueOf(date).matches(regexPatternDate);
    }
}
