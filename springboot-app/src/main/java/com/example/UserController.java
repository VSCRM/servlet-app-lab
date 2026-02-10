package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

interface UserRepository extends JpaRepository<User, Integer> {}

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public User getOne(@PathVariable int id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable int id, @RequestBody User details) {
        User user = repository.findById(id).orElseThrow();
        user.setName(details.getName());
        user.setEmail(details.getEmail());
        user.setPassword(details.getPassword());
        return repository.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        repository.deleteById(id);
    }
}