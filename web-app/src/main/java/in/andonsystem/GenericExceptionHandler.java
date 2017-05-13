package in.andonsystem;

import in.andonsystem.v2.dto.FieldError;
import in.andonsystem.v2.dto.RestError;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler {
    
    private final Logger logger = LoggerFactory.getLogger(GenericExceptionHandler.class);

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> processValidationError(MethodArgumentNotValidException ex) {
        logger.debug("processValidationError()");
        BindingResult result = ex.getBindingResult();
        return new ResponseEntity<>(processFieldError(result.getFieldErrors()), HttpStatus.BAD_REQUEST);
    }

    private List<FieldError> processFieldError(List<org.springframework.validation.FieldError> fieldErrors) {
        List<FieldError> errors = fieldErrors.stream()
                .map(error -> {
                    return new FieldError(error.getField(),
                            error.getRejectedValue(),
                            messageSource.getMessage(error.getCodes()[0], null, error.getDefaultMessage(), LocaleContextHolder.getLocale())
                    );

                })
                .collect(Collectors.toList());
        return errors;
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> bodyNotFound(HttpMessageNotReadableException e){
        logger.debug("bodyNotFound: {}",e.getMessage());
        return response(HttpStatus.BAD_REQUEST, 400, "request body is empty.", e.getMessage(), "");
    }

//    @ExceptionHandler
//    ResponseEntity<?> handleConflict(DataIntegrityViolationException e) {
//    	String cause = e.getRootCause().getMessage();
//    	if(cause.toLowerCase().contains("duplicate")){
//    		return response(HttpStatus.CONFLICT, 40901, "Duplicate Entry. Data with same name already exist in database.", e.getRootCause().getMessage(), "");
//    	}else if(cause.toLowerCase().contains("cannot delete")){
//    		return response(HttpStatus.CONFLICT, 40903, "Deletion restricted to prevent data inconsistency.", e.getRootCause().getMessage(), "");
//    	}
//        return response(HttpStatus.CONFLICT, 40900, "Operation cannot be performed. Integrity Constraint violated.", e.getRootCause().getMessage(), "");
//    }
    @ExceptionHandler
    ResponseEntity<?> handleException(Exception e) {
        logger.debug("handleException: {}",e.getMessage());
        e.printStackTrace();
        return response(HttpStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), e.getMessage(), "");
    }

    private ResponseEntity<RestError> response(HttpStatus status, int code, String msg) {
        return response(status, code, msg, "", "");
    }

    private ResponseEntity<RestError> response(HttpStatus status, int code, String msg, String devMsg, String moreInfo) {
        return new ResponseEntity<>(new RestError(status.value(), code, msg, devMsg, moreInfo), status);
    }

}
