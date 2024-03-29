package com.inscricaobehoh.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class BehohExceptionHandler extends ResponseEntityExceptionHandler{
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida",null, 
				LocaleContextHolder.getLocale());
		String mensagemDev = ex.getCause() != null ? ex.getCause().toString(): ex.toString();
		
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
		
		return handleExceptionInternal(ex, erros, headers, 
				HttpStatus.BAD_REQUEST, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	//Classe para tratar casso tente deletar um recurso que não existe
	@ExceptionHandler({EmptyResultDataAccessException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> EmptyResultDataAccessException(WebRequest request,
			EmptyResultDataAccessException ex) {
		
		String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", 
				null, LocaleContextHolder.getLocale());
		String mensagemDev = ex.toString();
		
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	//Metodo para capturar os erros e mostrar a mensagem para o usuario e desenvolvedor
	private List<Erro> criarListaDeErros(BindingResult bindingResult){
		List<Erro> erros = new ArrayList<>();
		
		for(FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSource.getMessage(fieldError, 
					LocaleContextHolder.getLocale());
			String mensagemDev = fieldError.toString();
			
			erros.add(new Erro(mensagemUsuario, mensagemDev));
		}
		return erros;
	}
	
	
	
	public static class Erro{
		private String mensagemUsuario;
		private String mensagemDev;
		
		public Erro(String mensagemUsuario, String mensagemDev) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDev = mensagemDev;
		}
		
		public String getMensagemUsuario() {
			return mensagemUsuario;
		}
		public void setMensagemUsuario(String mensagemUsuario) {
			this.mensagemUsuario = mensagemUsuario;
		}
		public String getMensagemDev() {
			return mensagemDev;
		}
		public void setMensagemDev(String mensagemDev) {
			this.mensagemDev = mensagemDev;
		}
		
		
	}
	
}
