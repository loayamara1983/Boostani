package com.boostani.backend.api.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.web.response.cache.CacheResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/cache")
public class CacheController {

//	@Autowired
	private CacheManager cacheManager;
	
	@ApiOperation(value = "Evicts all caches on memory.", response = CacheResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully cleared all caches"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/clearAll", method = RequestMethod.POST, headers = "Accept=application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CacheResponse> clearCache() {
		
		CacheResponse cacheResponse = new CacheResponse();
		
		cacheManager.getCacheNames().parallelStream().forEach(cacheName ->{
			cacheManager.getCache(cacheName).clear();
		});
		
		cacheResponse.setMessage("Caches cleared successfully");
		
		return new ResponseEntity<CacheResponse>(cacheResponse, HttpStatus.OK);
	}

}
