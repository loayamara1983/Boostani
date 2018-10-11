package com.boostani.backend.api.service.social;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.security.SocialUserDetailsService;

import com.boostani.backend.api.persistance.model.FacebookUser;

public interface SocialUserService extends SocialUserDetailsService, UserDetailsService {

	FacebookUser loadUserByConnectionKey(ConnectionKey connectionKey);
    
	UserDetails loadUserByUsername(String username);
    
    void updateUserDetails(FacebookUser user);
}
