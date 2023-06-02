package org.example.regex;

public class Regex {

    public boolean RegexIdentityNumber(String identityNumber) {

        String trimmedId = identityNumber.replaceAll("-", "");

        String regexId = "^[12][09]\\d{2}(0[1-9]|1[0-2])(0[1-9]|1\\d|2[0-9]|3[01])[0-9]{4}$";

        return trimmedId.matches(regexId);
    }

    public boolean RegexLetters(String name){
        String regex = "^[A-Za-z]+$";

        return name.matches(regex);
    }

    public boolean RegexNumbers(String numbersOnly){
        String regexAccount = "^[0-9]+$";

        return String.valueOf(numbersOnly).matches(regexAccount);
    }

    public boolean RegexDouble(String numbersOnly){
        String regexDouble = "^[0-9]+(\\.[0-9]+)?$";

        return String.valueOf(numbersOnly).matches(regexDouble);
    }

    public boolean RegexDate(String date){
        String regexPatternDate = "\\d{4}-\\d{2}-\\d{2}";

        return String.valueOf(date).matches(regexPatternDate);
    }
}
