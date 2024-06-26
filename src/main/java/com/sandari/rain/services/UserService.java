package com.sandari.rain.services;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sandari.rain.libraries.exceptions.RestException;
import com.sandari.rain.libraries.typings.enums.ErrorScope;
import com.sandari.rain.libraries.utils.UserInput;
import com.sandari.rain.models.User;
import com.sandari.rain.repositories.UserRepository;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> list() {
        return userRepository.findAll();
    }

    public Page<User> paginated(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public User create(UserInput userInput) {
        User existingUser = userRepository.findByUsername(userInput.getUsername());

        if(existingUser != null) {
            throw new RestException("Username is already taken.", 400, ErrorScope.USER);
        }

        User user = new User(userInput.getUsername(), userInput.getPassword(), userInput.getRole());
        return userRepository.saveAndFlush(user);
    }


    public User get(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }


    public User update(Long id, User user) {
        User existingUser = userRepository.getReferenceById(id);
        BeanUtils.copyProperties(user,existingUser,"speaker_id");
        return userRepository.saveAndFlush(existingUser);
    }

}
