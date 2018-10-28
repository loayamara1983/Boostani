package com.boostani.backend.api.service.storage;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.boostani.backend.api.persistance.model.User;
import com.boostani.backend.api.service.storage.exception.FileStorageException;
import com.boostani.backend.api.service.storage.exception.MyFileNotFoundException;
import com.boostani.backend.api.service.user.UserAlreadyFoundException;
import com.boostani.backend.api.service.user.UserCrudService;

@Service
public class FileStorageService {
	
	@Autowired
	private UserCrudService userService;

    public String storeFile(User currentUser, MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            currentUser.setProfileImage(file.getBytes());
            userService.save(currentUser);
            
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } catch (UserAlreadyFoundException e) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
		}
    }

    public Resource loadFileAsResource(User currentUser, String fileName) {
        Resource resource = new ByteArrayResource(currentUser.getProfileImage());
		if(resource.exists()) {
		    return resource;
		} else {
		    throw new MyFileNotFoundException("File not found " + fileName);
		}
    }
}
