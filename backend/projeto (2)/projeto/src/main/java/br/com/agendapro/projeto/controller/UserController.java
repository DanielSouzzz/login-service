package br.com.agendapro.projeto.controller;

import br.com.agendapro.projeto.repository.IUsuario;
import br.com.agendapro.projeto.model.User;
import br.com.agendapro.projeto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUsuario dao;
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> ListUser (){
        return ResponseEntity.status(200).body(userService.listUser());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser = dao.save(user);
        return ResponseEntity.status(201).body(newUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser (@RequestBody User user){
        User editUser = dao.save(user);
        return ResponseEntity.status(201).body(editUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser (@PathVariable Integer id) {
        Optional<User> userOptional = dao.findById(id);
        if (userOptional.isPresent()) {
            dao.deleteById(id);
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}
