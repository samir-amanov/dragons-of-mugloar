package com.samir.dragons.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class ApiResponseValidator {

	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = factory.getValidator();

	public <T> void validate(T response) {
		if (response == null) {
			throw new IllegalArgumentException("API response is null");
		}
		Set<ConstraintViolation<T>> violations = validator.validate(response);
		if (!violations.isEmpty()) {
			throw new IllegalStateException("API response is invalid: " + violations);
		}
	}

	public <T> void validateAll(List<T> responses) {
		if (responses == null) {
			throw new IllegalArgumentException("API response list is null");
		}
		for (T response : responses) {
			validate(response);
		}
	}
}