package com.madeeasy.error.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        Map<String, Object> errorResponse = Map.of("HTTP method not supported", status.value());
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * The handleHttpMediaTypeNotSupported method will be triggered when a client sends a request with an unsupported media type.
     * For example, if a client sends a request with a Content-Type that the server does not support, such as application/xml,
     * and the server does not have a corresponding handler for it, the handleHttpMediaTypeNotSupported method will be invoked
     * to handle the exception.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        Map<String, Object> errorResponse = Map.of("Unsupported media type", status.value());
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * The HttpMediaTypeNotAcceptableException is typically triggered when the client request specifies an
     * unsupported media type in the Accept header, and the server cannot provide a response in any of the
     * requested media types.
     * example :
     *
     * @GetMapping(value = "/serialize")
     * public ResponseEntity<Object> getExample() {
     * // Assume there is an error while serializing the response object
     * SomeObject object = new SomeObject();
     * return ResponseEntity.ok(object);
     * }
     * and :=>
     * public class SomeObject { }
     * //----------------------------
     * solution :=>
     * //----------------------------
     * @GetMapping(value = "/serialize")
     * public ResponseEntity<Object> getExample() {
     * // Assume there is an error while serializing the response object
     * SomeObject object = new SomeObject();
     * object.setEmail("sdf@gmil.com");
     * object.setName("pabitra");
     * return ResponseEntity.ok(object);
     * }
     * @Getter
     * @Setter public class SomeObject {
     * private String name;
     * private String email;
     * }
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers,
                                                                      HttpStatusCode status,
                                                                      WebRequest request) {
        Map<String, Object> errorResponse = Map.of("Requested media type is not acceptable.", status.value(),
                "detail", "Acceptable representations: [application/json, application/*+json].");
        return new ResponseEntity<>(errorResponse, headers, status);
    }


    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
                                                               WebRequest request) {
        Map<String, Object> errorResponse = Map.of("Path variable is missing", status.value());
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * )-----------------------------------(@RequestParam("param") String paramValue)-----------------------------------
     * when a request is made with a missing request parameter, such as 'par' instead of 'param', or '/endpoint?' the
     * handleMissingServletRequestParameter method will be triggered,
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        String parameterName = ex.getParameterName();
        String errorMessage = "Request parameter '" + parameterName + "' is missing.";
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);

        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * This method will be triggered when a required part of the request is missing. For example, if you have a
     * file upload endpoint and the client does not include the file part in the request, the MissingServletRequestPartException
     * will be thrown, and this method will handle it by returning a response with an appropriate error message.
     * <p>
     * This exception is thrown when a required part of a multipart request (such as a file upload) is missing.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        Map<String, Object> errorResponse = Map.of("Required parameter '" + ex.getRequestPartName() + "'is missing", status.value());
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * This exception is thrown when there is an error binding the request parameters or headers to the method parameters.
     * ex:  @GetMapping("/example")
     * public ResponseEntity<String> exampleMethod(@RequestParam("param") String paramValue) {
     * // Method implementation
     * }
     * In this example, the exampleMethod expects a request parameter called "param". However, if a request is made to
     * /example without providing the "param" parameter, a ServletRequestBindingException will be thrown.
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        String errorMessage = "Error binding request parameters or headers.";
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * This exception is thrown when there are validation errors for method arguments annotated with validation constraints such as @Valid.
     *
     * @NotBlank private String name;
     * <p>
     * The name field of the above  is annotated with @NotBlank, indicating that it should not be empty.
     * If a request is made with an empty name field in the request body, a MethodArgumentNotValidException will be thrown.
     */
    // not understood
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request
    ) {
        String errorMessage = "Validation failed for method argument.";
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);

    }


    /**
     * @GetMapping("/example") public ResponseEntity<String> exampleMethod() {
     * // Method implementation
     * }
     * In this example, the exampleMethod handles requests made to '/example'. However, if a request is made to a
     * non-existing URL, such as '/non-existing', a NoHandlerFoundException will be thrown as there is no matching handler for that URL.
     */

    // not understood
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatusCode status,
                                                                   WebRequest request) {
        String errorMessage = "No handler found for the requested URL i.e. not uri found";
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
                                                                        HttpHeaders headers,
                                                                        HttpStatusCode status,
                                                                        WebRequest request) {
        return super.handleAsyncRequestTimeoutException(ex, headers, status, request);
    }

    /**
     * let's assume you have a REST API endpoint that retrieves customer data by ID. If the requested customer ID does not
     * exist in the system, an ErrorResponseException can be thrown.
     */
    @Override
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(errorMessage, status);
    }


    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String errorMessage = "Conversion not supported.";
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);

    }

    /**
     * @GetMapping("/example/{id}") public ResponseEntity<String> exampleMethod( @PathVariable("id") Long id) {
     * // Method implementation
     * return ResponseEntity.ok("message sent : " + id);
     * }
     * If you make a request to this endpoint with a value for id that cannot be converted to a Long,
     * such as a string or an invalid number, the TypeMismatchException will be thrown, and the handleTypeMismatch
     * method will be triggered to handle the exception
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatusCode status,
                                                        WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameter i.e. required @PathVariable(\"id\") Long id ");

    }

    /**
     * The handleHttpMessageNotReadable method is used to handle the scenario when the HTTP message (request body)
     * cannot be read or parsed correctly. This typically occurs when the request body contains invalid or malformed JSON data.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        Map<String, Object> errorResponse = Map.of("Invalid JSON data in request body", status.value());
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * This can occur when there is an issue with serializing or writing the response body.
     * . If any class  does not have appropriate serialization annotations or does not implement Serializable,
     * it will trigger the handleHttpMessageNotWritable method.
     * <p>
     * if there is an issue serializing the response object to JSON or if the response body cannot be written.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> errorResponse = Map.of("there is an issue serializing the response object to JSON or" +
                " the response body cannot be written.", status.value());
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * This method is called internally when generating error responses.
     * The createProblemDetail method will be triggered when an exception occurs during request processing and Spring's
     * error handling mechanism is activated. The specific scenarios and exceptions that trigger this method depend on your
     * application's error handling configuration and the types of exceptions thrown during request processing.
     */

    @Override
    protected ProblemDetail createProblemDetail(Exception ex,
                                                HttpStatusCode status,
                                                String defaultDetail,
                                                String detailMessageCode,
                                                Object[] detailMessageArguments,
                                                WebRequest request) {
        return super.createProblemDetail(ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
    }

    /**
     * Whenever an unhandled exception occurs within your application, this method will be triggered. You can perform any
     * custom handling or logging you need within this method. You can also modify the response body, headers, or status as required.
     * In the example, we create an ErrorResponse object with a custom error message and status code, and return it as the response entity.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body,
                                                          HttpHeaders headers,
                                                          HttpStatusCode statusCode,
                                                          WebRequest request) {
        return super.createResponseEntity(body, headers, statusCode, request);
    }
}
