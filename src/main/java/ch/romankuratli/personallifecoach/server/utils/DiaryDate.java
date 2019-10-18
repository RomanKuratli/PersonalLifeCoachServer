package ch.romankuratli.personallifecoach.server.utils;

import org.apache.velocity.runtime.directive.Parse;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DiaryDate {
    public static final String DELIM = "_";
    private int year;
    private int month;
    private int day;
    private Date utilDate;

    public static DiaryDate fromString(String s) throws ParseException {
        int[] params = new int[3];
        String[] input = s.split(DELIM);
        if (input.length != 3) throw new ParseException("cannot convert to a DiaryDate: " + s, 0);
        for (int i = 0; i < 3; i++) {
            params[i] = Integer.valueOf(input[i]);
        }
        return new DiaryDate(params[0], params[1], params[2]);
    }

    private DiaryDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        this.utilDate = c.getTime();
    }

    public Date getUtilDate() {
        return this.utilDate;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String toString() {
        return "" + this.year + DELIM + this.month + DELIM + this.day;
    }
}
