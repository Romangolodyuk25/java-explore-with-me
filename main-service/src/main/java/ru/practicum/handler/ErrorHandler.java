package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ApiError;
import ru.practicum.exception.*;

import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse throwableHandler(Throwable e) {
        log.error("500 {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ErrorResponse("Ошибка прогарммы", e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации {}",e.getMessage(), e);
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.error("Ошибка валидации {}",e.getMessage(), e);
        return new ErrorResponse("Не был передан параметр", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNumberFormatException(final NumberFormatException e) {
        log.error("Запрос составлен некорректно {}",e.getMessage(), e);
        return new ErrorResponse("Запрос составлен некорректно", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError constraintViolationExceptionException(final ConstraintViolationException e) {
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.CONFLICT.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError dataIntegrityViolationExceptionException(final DataIntegrityViolationException e) {
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.CONFLICT.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError userNotExistHandler(UserNotExistException e) {
        log.error("Юзер не найден {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.BAD_REQUEST.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError categoryNotExistHandler(CategoryNotExistException e) {
        log.error("Категория не найдена {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.NOT_FOUND.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError eventNotExistHandler(EventNotExistException e) {
        log.error("Событие не найдено {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.BAD_REQUEST.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError compilationNotExistHandler(CompilationNotExistException e) {
        log.error("Подборка не найдена {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.BAD_REQUEST.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError requestNotExistHandler(RequestNotExistException e) {
        log.error("Запрос не найден {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.BAD_REQUEST.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError commentNotExistHandler(CommentNotExistException e) {
        log.error("комментарий не найден {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.BAD_REQUEST.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventDoesNotSatisfyRulesException(EventDoesNotSatisfyRulesException e) {
        log.error("Событие не удовлетворяет правилам редактирования {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.FORBIDDEN.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestDoesNotSatisfyRulesException(RequestDoesNotSatisfyRulesException e) {
        log.error("Запрос не удовлетворяет правилам создания {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.FORBIDDEN.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError compilationDoesNotSatisfyRulesException(CompilationDoesNotSatisfyRulesException e) {
        log.error("Подборка не удовлетворяет правилам создания {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.FORBIDDEN.name(), LocalDateTime.now().format(formatter));
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError categoryRelationHandler(CategoryRelationWithEventException e) {
        log.error("Категория связана с событием {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.CONFLICT.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError commentDoesSatisfyRulesHandler(CommentDoesNotSatisfyRulesException e) {
        log.error("Комментарий не удовлетворяет правилам создания {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.CONFLICT.name(), LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError userIsNotOwnerHandler(UserIsNotOwner e) {
        log.error("Юзер не является владельцем {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(e.getMessage(), stackTrace, HttpStatus.CONFLICT.name(), LocalDateTime.now().format(formatter));
    }

    private String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
