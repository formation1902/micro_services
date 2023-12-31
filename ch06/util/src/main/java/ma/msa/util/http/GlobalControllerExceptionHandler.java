package ma.msa.util.http;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ma.msa.api.exceptions.InvalidInputException;
import ma.msa.api.exceptions.NotFoundException;

@RestControllerAdvice
class GlobalControllerExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public @ResponseBody HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, NotFoundException ex) {
     /*
     * Arguments :
     *    - ServerHttpRequest request 
     *    - NotFoundException ex
     * Return :
     *    @ResponseBody HttpErrorInfo
     */
    return createHttpErrorInfo(NOT_FOUND, request, ex);
  }

  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public @ResponseBody HttpErrorInfo handleInvalidInputException( ServerHttpRequest request, InvalidInputException ex) {
    /*
     * Arguments :
     *    - ServerHttpRequest request 
     *    - InvalidInputException ex
     * Return :
     *    @ResponseBody HttpErrorInfo
     */
    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {
    /*
     * Arguments:
     *    - HttpStatus httpStatus
     *    - ServerHttpRequest request
     *    - Exception ex
     * Return:
     *    HttpErrorInfo
     */
    
    final String path = request.getPath().pathWithinApplication().value();
    final String message = ex.getMessage();

    logger.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
    return new HttpErrorInfo(httpStatus, path, message);
  }
}
