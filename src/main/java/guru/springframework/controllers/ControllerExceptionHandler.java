package guru.springframework.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	@ExceptionHandler(NumberFormatException.class)
//	public ModelAndView handleNumberFormat(Exception exception) {
//		log.warn("Bad request", exception);
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("400error");
//		modelAndView.addObject("exception", exception);
//		return modelAndView;
//	}
}
