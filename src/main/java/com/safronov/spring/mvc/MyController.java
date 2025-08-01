package com.safronov.spring.mvc;

import com.safronov.spring.mvc.untitled.entity.Employee;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контроллер для обработки запросов, связанных с информацией о сотрудниках
 */
@Slf4j
@Controller
public class MyController {

    /**
     * Перенаправляет на главную страницу
     */
    @GetMapping("/")
    public String showHomePage() {
        log.info("Перенаправление на главную страницу");
        return "redirect:/index.html";
    }

    /**
     * Отображает форму для ввода данных сотрудника
     */
    @GetMapping("/askDetails")
    public String showEmployeeForm(@ModelAttribute("employee") Employee employee) {
        log.info("Отображение формы для ввода данных сотрудника");
        return "redirect:/askDetails.html";
    }

    /**
     * Обрабатывает отправку формы с данными сотрудника
     * Валидирует данные и перенаправляет на соответствующую страницу
     */
    @PostMapping("/showDetails")
    public String processEmployeeDetails(@Valid @ModelAttribute("employee") Employee employee,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации в данных сотрудника: {}", bindingResult.getAllErrors());
            return "redirect:/askDetails.html";
        }
        
        log.info("Данные сотрудника успешно обработаны: {}", employee.getEmployeeName());
        return "redirect:/showDetails.html";
    }
}
