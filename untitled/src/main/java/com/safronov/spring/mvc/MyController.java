package com.safronov.spring.mvc;

import com.safronov.spring.mvc.untitled.entity.Employee;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyController {

    @RequestMapping("/")
    public String showFirstView(){
        return "myView";
    }

    @RequestMapping("askDetails")
    public String askEmployeeDetails(@ModelAttribute("employee") Employee employee) {
        return "ask-emp-details-view";
    }

    @RequestMapping("showDetails")
    public String showEmpDetails(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "ask-emp-details-view";
        }
        return "show-emp-details-view";
    }
}
