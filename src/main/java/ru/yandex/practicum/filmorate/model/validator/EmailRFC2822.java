package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailRFC2822ConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailRFC2822 {
    String message() default "Некорректный адрес электронной почты. Почта не соответствует паттерну rfc2822";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
