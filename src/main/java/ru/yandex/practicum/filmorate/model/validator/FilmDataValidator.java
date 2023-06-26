package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented //Указывает, что помеченная таким образом аннотация должна быть добавлена в javadoc поля/метода.
//Список реализаций данного интерфейса.
@Constraint(validatedBy = FilmDateConstraintValidator.class)
//Указывает, что именно мы можем пометить этой аннотацией
//ElementType.METHOD – только для методов;
//ElementType.FIELD – только для атрибутов(переменных) класса;
@Target({ElementType.METHOD, ElementType.FIELD})
//Позволяет указать жизненный цикл аннотации: будет она присутствовать только в исходном коде,
// в скомпилированном файле, или она будет также видна и в процессе выполнения.
//RetentionPolicy.RUNTIME – будет присутствовать только в момент выполнения;
@Retention(RetentionPolicy.RUNTIME)
public @interface FilmDataValidator {
    String message() default "Дата релиза — не может быть раньше 28 декабря 1895 года.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}