package de.unibayreuth.se.taskboard.business.impl;

import de.unibayreuth.se.taskboard.business.domain.User;
import de.unibayreuth.se.taskboard.business.exceptions.UserNotFoundException;
import de.unibayreuth.se.taskboard.business.ports.UserPersistenceService;
import de.unibayreuth.se.taskboard.business.ports.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    // I'm using UserPersistenceService to handle database operations
    private final UserPersistenceService userPersistenceService;

    @Override
    @NonNull
    public List<User> getAllUsers() {
        return userPersistenceService.getAll();
    }

    @Override
    public User getUserById(@NonNull UUID id) throws UserNotFoundException {
        return userPersistenceService.getById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " does not exist."));
    }

    @Override
    @NonNull
    public User createUser(@NonNull User user) {
        // it creates if new or updates if ID exists
        return userPersistenceService.upsert(user);
    }

    @Override
    public void clear() {
        //for testing part i add clears all users from database
        userPersistenceService.clear();
    }
}