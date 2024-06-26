package br.com.agendapro.projeto.controller;

import br.com.agendapro.projeto.DAO.IUsuario;
import br.com.agendapro.projeto.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUsuario dao;

    @GetMapping
    public List<User> ListUser (){
        return (List<User>)dao.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user){
        User newUser = dao.save(user);
        return newUser;
    }

    @PutMapping
    public User updateUser (@RequestBody User user){
        User editUser = dao.save(user);
        return editUser;
    }

    @DeleteMapping("/{id}")
    public User deleteUser (@PathVariable Integer id) throws NoSuchFieldException {
        Optional<User> userOptional = dao.findById(id);
        if (userOptional.isPresent()) {
            User delUser = userOptional.get();
            dao.deleteById(id);
            return delUser;
        }else {
            throw new NoSuchFieldException("User not found with id: " + id);
        }
    }
}
