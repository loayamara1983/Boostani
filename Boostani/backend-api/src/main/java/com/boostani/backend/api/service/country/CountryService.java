package com.boostani.backend.api.service.country;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistance.model.Country;

/**
 * 
 * @author Loay
 *
 */

@Service
@CacheConfig(cacheNames="countries")
public class CountryService {

	private List<Locale> availableLocales = Arrays.asList(Locale.getAvailableLocales());
	
	/**
	 * 
	 * @return
	 */
	@Cacheable
	public List<Country> findAll() {
		// Get all available locales

		// Get all available ISO countries
		String[] countryCodes = Locale.getISOCountries();

		// Create a collection of all available countries
		List<Country> countries = new ArrayList<Country>();

		// Map ISO countries to custom country object
		for (String countryCode : countryCodes) {

			Optional<Locale> candidate = availableLocales.stream().filter(l -> l.getCountry().equals(countryCode))
					.collect(Collectors.reducing((a, b) -> null));

			Locale locale;
			if (candidate.isPresent()) {
				locale = candidate.get();
			} else {
				locale = new Locale("", countryCode);
			}

			String iso = locale.getISO3Country();
			String code = locale.getCountry();
			String country = locale.getDisplayCountry(locale);
			
			Currency currency = Currency.getInstance(locale);
			
			countries.add(new Country(iso, code, country, currency));
		}

		// Sort countries
		Collections.sort(countries);

		return countries;
	}
	
	/**
	 * 
	 * @param countryCode
	 * @return
	 */
	public String currency(String countryCode) {
		Optional<Locale> candidate = availableLocales.stream().filter(l -> l.getCountry().equals(countryCode))
				.collect(Collectors.reducing((a, b) -> null));

		Locale locale;
		if (candidate.isPresent()) {
			locale = candidate.get();
		} else {
			locale = new Locale("", countryCode);
		}
		
		return Currency.getInstance(locale).getCurrencyCode();
	}
}
