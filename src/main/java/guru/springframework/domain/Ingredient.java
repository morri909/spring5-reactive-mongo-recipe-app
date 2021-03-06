package guru.springframework.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Ingredient {
	private String id = UUID.randomUUID().toString();
	private String description;
	private BigDecimal amount;
	private Recipe recipe;
	private UnitOfMeasure unitOfMeasure;

	public Ingredient() {
	}

	public Ingredient(String description, BigDecimal amount, UnitOfMeasure unitOfMeasure) {
		this.description = description;
		this.amount = amount;
		this.unitOfMeasure = unitOfMeasure;
	}

	public Ingredient(String description, BigDecimal amount, UnitOfMeasure unitOfMeasure, Recipe recipe) {
		this.id = id;
		this.description = description;
		this.amount = amount;
		this.recipe = recipe;
		this.unitOfMeasure = unitOfMeasure;
	}

}
