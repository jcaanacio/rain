package com.sandari.rain.services;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sandari.rain.libraries.exceptions.RestException;
import com.sandari.rain.libraries.typings.enums.ErrorScope;
import com.sandari.rain.libraries.utils.ProfileInput;
import com.sandari.rain.models.Profile;
import com.sandari.rain.models.User;
import com.sandari.rain.repositories.ProfileRepository;
import com.sandari.rain.repositories.UserRepository;



@Service
public class ProfileService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public List<Profile> list() {
        return profileRepository.findAll();
    }

    public Page<Profile> paginated(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return profileRepository.findAll(pageable);
    }

    public Profile create(Long userId, ProfileInput profileInput) {
        User user = userRepository.findById(userId).orElse(null);

        if(user == null) {
            throw new RestException("User not found.", 404, ErrorScope.CLIENT);
        }

        Optional<Profile> existingProfile = profileRepository.findByUserUserId(userId);

        if(existingProfile.isPresent()) {
            throw new RestException("Profile is already registered using userId.", 404, ErrorScope.CLIENT);
        }

        Profile profile = new Profile(user, profileInput.getFirstName(), profileInput.getLastName(), profileInput.getEmail(), profileInput.getPhone());
        return profileRepository.saveAndFlush(profile);
    }


    public Profile get(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    public Profile getByUserId(Long userId) {
        return profileRepository.findByUserUserId(userId).orElse(null);
    }

    @Transactional
    public void delete(Long userId, Long profileId) {
        Profile profile = profileRepository.findByUserUserId(userId).orElse(null);

        if(profile == null) {
            throw new RestException("Profile not found.", 404, ErrorScope.CLIENT);
        }

        if (profile.getProfileId() != profileId) {
            throw new RestException("Profile id mismatched.", 404, ErrorScope.CLIENT);
        }

        profileRepository.deleteById(profile.getProfileId());
    }

    public Profile update(Long userId, Long profileId, ProfileInput profileInput){
        Profile profile = profileRepository.findByUserUserId(userId).orElse(null);

        if(profile == null) {
            throw new RestException("Profile not found.", 404, ErrorScope.CLIENT);
        }

        if (profile.getProfileId() != profileId) {
            throw new RestException("Profile id mismatched.", 404, ErrorScope.CLIENT);
        }

        updateNonNullFields(profileInput, profile);
        return profileRepository.saveAndFlush(profile);
    }

    private void updateNonNullFields(Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(source);

                if (value != null) {
                    Field targetField = target.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            } catch (Exception e) {
                // Handle exceptions
            }
        }
    }
}
