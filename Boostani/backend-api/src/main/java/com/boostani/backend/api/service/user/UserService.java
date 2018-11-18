package com.boostani.backend.api.service.user;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.merchant.MerchantsService;

@Service
@CacheConfig(cacheNames = "users")
public class UserService extends MerchantsService {

	public boolean createAffliate(User user) throws UserNotFoundException {
		try {
			String sessionId = getAdminSessionId();
			if (StringUtils.isBlank(sessionId)) {
				throw new UserNotFoundException("Unauthorized access to Boostani Backend");
			}

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

			String createRequestData = env.getProperty("com.boostani.request.affilate.create");
			String formData = String.format(createRequestData, user.getUsername(), "", user.getFirstName(),
					user.getLastName(), "P", UUID.randomUUID().toString(), user.getCountry(), user.getPhoneNumber(),
					sessionId);
			map.add("D", formData);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, getDefaultHeaders());

			ResponseEntity<Object> createResponse = restTemplate.postForEntity(getDefaultUrl(), request, Object.class);
			return createResponse.getStatusCodeValue() == 200;

		} catch (RestClientException e) {
			e.printStackTrace();
			return false;
		}
	}
}
