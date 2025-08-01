package com.safronov.spring.mvc;

import com.safronov.spring.mvc.untitled.entity.Employee;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MyController {

    @GetMapping("/")
    public String showFirstView(){
        return "redirect:/index.html";
    }

    @GetMapping("/askDetails")
    public String askEmployeeDetails(@ModelAttribute("employee") Employee employee) {
        return "redirect:/askDetails.html";
    }

    @PostMapping("/showDetails")
    public String showEmpDetails(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "redirect:/askDetails.html";
        }
        return "redirect:/showDetails.html";
    }
}
