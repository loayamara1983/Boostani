package com.boostani.backend.api.persistance.model;

import java.util.Currency;

/**
 * 
 * @author Loay
 *
 */
public class Country implements Comparable<Country> {

    private String iso;
    private String code;
    private String name;
    private Currency currency;

    public Country(String iso, String code, String name, Currency currency) {
        this.iso = iso;
        this.code = code;
        this.name = name;
        this.currency = currency;
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
    
    public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
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
