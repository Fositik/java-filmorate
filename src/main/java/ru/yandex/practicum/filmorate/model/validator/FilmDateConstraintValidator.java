package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

//Интерфейс ConstraintValidator принимает два типа: тип аннотации который будет поддерживаться,
// и тип свойства, который он проверяет (в данном примере, дату, LocalDate).
public class FilmDateConstraintValidator implements ConstraintValidator<FilmDataValidator, LocalDate> {

    //Минимально допустимой датой в нашем случае считается 28 февраля 1895г.
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    //этот метод пустой и не имеет реализации, но он может быть использован,
    // для того чтобы сохранить данные из аннотации, во второй аннотации нам пригодится этот метод и мы его реализуем.
    public void initialize(FilmDataValidator constraintAnnotation) {
        //Это оставим пустым, так как нет необходимости инициализировать аннотацию.
    }

    @Override
    //основную логику проверки выполняет метод isValid(LocalDate releaseDate, ConstraintValidatorContext context).
    // Значение поля передается в качестве первого аргумента
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        // Если дата равна null, она считается valid,
        // поскольку у нас есть другие аннотации для проверки на null.
        if (releaseDate == null) {
            return true;
        }

        // Дата valid, если она после MIN_RELEASE_DATE.
        return !releaseDate.isBefore(MIN_RELEASE_DATE);
    }
}
