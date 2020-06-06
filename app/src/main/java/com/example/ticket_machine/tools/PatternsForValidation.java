package com.example.ticket_machine.tools;

import java.util.regex.Pattern;

public final class PatternsForValidation {
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&*()+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    public static final Pattern NAME_PATTERN = Pattern.compile("^" +
            "(?=.*[a-zA-Z])" +        //any letter
            "(?=\\S+$)" +             //no white spaces
            ".{4,25}" +               //at least 4 characters, max 25 characters
            "$");

    public static final Pattern LAST_NAME_PATTERN = Pattern.compile("^" +
            "(?=.*[a-zA-Z])" +        //any letter
            "(?=\\S+$)" +             //no white spaces
            ".{4,35}" +               //at least 4 characters, max 35 characters
            "$");

    public static final Pattern TITLE_PATTERN = Pattern.compile("^" +
            "(?=.*[a-zA-Z])" +        //any letter
            ".{2,25}" +               //at least 2 characters, max 25 characters
            "$");

    public static final Pattern DESCRIPTION_PATTERN = Pattern.compile("^" +
            "(?=.*[a-zA-Z])" +        //any letter
            ".{5,50}" +               //at least 5 characters, max 50 characters
            "$");


}
