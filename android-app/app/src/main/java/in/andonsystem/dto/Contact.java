package in.andonsystem.dto;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by mdzahidraza on 19/06/17.
 */

public class Contact implements Comparable<Contact>{
    private String name;
    private String number;

    public Contact() {
    }

    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int compareTo(@NonNull Contact other) {
        return this.getName().toLowerCase().compareTo(other.getName().toLowerCase());
    }
}
