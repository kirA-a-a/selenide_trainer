package com.safronov.spring.mvc.untitled.entity;

import com.safronov.spring.mvc.untitled.validation.CheckEmail;
import jakarta.validation.constraints.*;

public class Employee {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё\\s]+", message = "Имя должно содержать только буквы")
    private String employeeName;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов")
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё\\s]+", message = "Фамилия должна содержать только буквы")
    private String employeeSurname;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введите корректный email адрес")
    @CheckEmail
    private String email;

    @NotEmpty(message = "Выберите версию Java")
    private String javaVersion;

    @NotEmpty(message = "Укажите опыт в автотестировании")
    private String autoTestExperience;

    @NotEmpty(message = "Выберите хотя бы один язык программирования")
    private String[] programmingLanguages;

    // Конструкторы
    public Employee() {}

    public Employee(String employeeName, String employeeSurname, String email, String javaVersion, String autoTestExperience, String[] programmingLanguages) {
        this.employeeName = employeeName;
        this.employeeSurname = employeeSurname;
        this.email = email;
        this.javaVersion = javaVersion;
        this.autoTestExperience = autoTestExperience;
        this.programmingLanguages = programmingLanguages;
    }

    // Геттеры и сеттеры
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeSurname() {
        return employeeSurname;
    }

    public void setEmployeeSurname(String employeeSurname) {
        this.employeeSurname = employeeSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getAutoTestExperience() {
        return autoTestExperience;
    }

    public void setAutoTestExperience(String autoTestExperience) {
        this.autoTestExperience = autoTestExperience;
    }

    public String[] getProgrammingLanguages() {
        return programmingLanguages;
    }

    public void setProgrammingLanguages(String[] programmingLanguages) {
        this.programmingLanguages = programmingLanguages;
    }
} 