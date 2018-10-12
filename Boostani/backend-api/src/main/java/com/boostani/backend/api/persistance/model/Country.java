package com.boostani.backend.api.persistance.model;

/**
 * 
 * @author Loay
 *
 */
public class Country implements Comparable<Country> {

    private String iso;
    private String code;
    private String name;

    public Country(String iso, String code, String name) {
        this.iso = iso;
        this.code = code;
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Country o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "Country{" +
                "iso='" + iso + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
