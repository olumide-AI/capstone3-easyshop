package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile getProfileByUserId(int userId);
    Profile updateProfile(int userId, Profile profile);

}
