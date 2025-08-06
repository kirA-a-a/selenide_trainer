package com.safronov.spring.mvc;

import com.safronov.spring.mvc.untitled.entity.Employee;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контроллер для обработки запросов, связанных с информацией о сотрудниках
 */
@Controller
public class MyController {

    private static final Logger log = LoggerFactory.getLogger(MyController.class);

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
        
        log.info("Данные сотрудника успешно обработаны: {}", employee.getEmployeeName() != null ? employee.getEmployeeName() : "неизвестно");
        return "redirect:/showDetails.html";
    }

    /**
     * Перенаправляет на страницу с Basic авторизацией
     */
    @GetMapping("/basic-auth")
    public String showBasicAuthPage() {
        log.info("Перенаправление на страницу с Basic авторизацией");
        return "redirect:/basic_authentication.html";
    }
}
