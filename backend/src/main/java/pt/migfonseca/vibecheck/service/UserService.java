package pt.migfonseca.vibecheck.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.UserDTO;
import pt.migfonseca.vibecheck.model.User;
import pt.migfonseca.vibecheck.repo.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<UserDTO> getLeaderBoard() {
        return repository
                .findAll(Sort.by(Sort.Direction.ASC, "score"))
                .stream()
                .map((user) -> new UserDTO(user))
                .toList();
    }

    public UserDTO createUser(String username, String password) {
        User newUser = new User(username, password);
        repository.save(new User(username, password));
        return new UserDTO(newUser);
    }

}
