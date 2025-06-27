package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

// @Stanislav Hryshchuk assisted with Debugging profile controller and changes to the backedn
/**
 * ProfileController exposes REST endpoints for authenticated users
 * to view and update their profile information. Profile data is
 * always tied to the currently logged-in user.
 */
@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     * @param principal the authenticated user's principal.
     * @return the Profile of the user.
     * @throws ResponseStatusException if the profile is not found or an error occurs.
     */
    @GetMapping()
    public Profile getProfileByUserId(Principal principal){
        Profile profile;

        try {
            // get the currently logged username

            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            profile = profileDao.getProfileByUserId(userId);
            if (profile == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "profile not found");
            }
        } catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting profile ", e);
        }
        return profile;

    }

    /**
     * Updates the profile of the currently authenticated user.
     * @param profile the new profile data to update.
     * @param principal the authenticated user's principal.
     * @throws ResponseStatusException if the profile does not exist or an error occurs.
     */
    @PutMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    public void updateProfile(@RequestBody Profile profile, Principal principal){

        String userName = principal.getName();
        // find database user by userId
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        boolean success = profileDao.updateProfile(userId, profile);
        if(! success){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile UPDATE not found");
        }

    }
}
