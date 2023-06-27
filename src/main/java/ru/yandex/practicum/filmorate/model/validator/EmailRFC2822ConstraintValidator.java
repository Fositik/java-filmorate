package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

//Интерфейс ConstraintValidator принимает два типа: тип аннотации который будет поддерживаться,
// и тип свойства, который он проверяет
public class EmailRFC2822ConstraintValidator implements ConstraintValidator<EmailRFC2822, String> {

    //Почта должна соответствовать паттерну rfc2822
    private static final Pattern rfc2822 = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

    @Override
    //этот метод пустой и не имеет реализации, но он может быть использован,
    // для того чтобы сохранить данные из аннотации, во второй аннотации нам пригодится этот метод и мы его реализуем.
    public void initialize(EmailRFC2822 constraintAnnotation) {
        //Это оставим пустым, так как нет необходимости инициализировать аннотацию.
    }

    @Override
    // Значение поля передается в качестве первого аргумента
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Если email раевн null, он считается valid,
        // поскольку у нас есть другие аннотации для проверки на null.
        if (email == null) {
            return true;
        }

        return rfc2822.matcher(email).matches();
    }
}
