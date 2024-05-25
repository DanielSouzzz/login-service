package br.com.agendapro.projeto.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Control {
    @GetMapping("")
    public String mensagem(){
        return "Hello World!";
    }


}
