package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

	private final IngredientCommandToIngredient ingredientCommandToIngredient;
	private final IngredientToIngredientCommand ingredientToIngredientCommand;
	private final RecipeRepository recipeRepository;
	private final UnitOfMeasureRepository unitOfMeasureRepository;

	public IngredientServiceImpl(IngredientCommandToIngredient ingredientCommandToIngredient, IngredientToIngredientCommand ingredientToIngredientCommand, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
		this.ingredientCommandToIngredient = ingredientCommandToIngredient;
		this.ingredientToIngredientCommand = ingredientToIngredientCommand;
		this.recipeRepository = recipeRepository;
		this.unitOfMeasureRepository = unitOfMeasureRepository;
	}

	@Override
	public IngredientCommand findByRecipeIdAndId(String recipeId, String id) {
		Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);

		if (!recipeOptional.isPresent()) {
			log.error("Unable to find ingredient for recipe id: " + recipeId);
			return null;
		}

		Recipe recipe = recipeOptional.get();
		Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
				.filter(ingredient -> ingredient.getId().equals(id))
				.map(ingredient -> ingredientToIngredientCommand.convert(ingredient))
				.findFirst();

		if (!ingredientCommandOptional.isPresent()) {
			log.error("Unable to find ingredient id: " + id);
			return null;
		}
		return ingredientCommandOptional.get();
	}

	@Override
	@Transactional
	public IngredientCommand saveIngredientCommand(IngredientCommand ingredientCommand) {
		Optional<Recipe> recipeOptional = recipeRepository.findById(ingredientCommand.getRecipeId());
		if (!recipeOptional.isPresent()) {
			log.error("Unable to find recipe id: " + ingredientCommand.getRecipeId());
			return null;
		}
		Recipe recipe = recipeOptional.get();
		Optional<Ingredient> ingredientOptional = recipe.getIngredients()
				.stream()
				.filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
				.findFirst();

		if (ingredientOptional.isPresent()) {
			Ingredient ingredient = ingredientOptional.get();
			ingredient.setDescription(ingredientCommand.getDescription());
			ingredient.setAmount(ingredientCommand.getAmount());
			Optional<UnitOfMeasure> unitOfMeasureOptional = unitOfMeasureRepository.findById(ingredientCommand.getUnitOfMeasure().getId());
			if (!unitOfMeasureOptional.isPresent()) {
				log.error("Unable to find uom: " + ingredientCommand.getUnitOfMeasure().getId());
				return null;
			}
			ingredient.setUnitOfMeasure(unitOfMeasureOptional.get());
		} else {
			Ingredient ingredient = ingredientCommandToIngredient.convert(ingredientCommand);
			ingredient.setRecipe(recipe);
			recipe.addIngredient(ingredient);
		}

		Recipe savedRecipe = recipeRepository.save(recipe);

		Optional<Ingredient> savedIngredient = savedRecipe.getIngredients().stream()
				.filter(recipeIngredients -> recipeIngredients.getId().equals(ingredientCommand.getId()))
				.findFirst();

		if (!savedIngredient.isPresent()) {
			savedIngredient = savedRecipe.getIngredients().stream()
					.filter(ingredient -> ingredient.getDescription().equals(ingredientCommand.getDescription()))
					.filter(ingredient -> ingredient.getUnitOfMeasure().getId().equals(ingredientCommand.getUnitOfMeasure().getId()))
					.filter(ingredient -> ingredient.getAmount().equals(ingredientCommand.getAmount()))
					.findFirst();
		}

		return ingredientToIngredientCommand.convert(savedIngredient.get());
	}

	@Override
	public void deleteById(String recipeId, String id) {
		Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
		if (!recipeOptional.isPresent()) {
			log.warn("Unable to find recipe: " + recipeId);
			return;
		}
		Recipe recipe = recipeOptional.get();
		Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
				.filter(ingredient -> ingredient.getId().equals(id))
				.findFirst();

		if (!ingredientOptional.isPresent()) {
			log.warn("Unable to find ingredient: " + id);
			return;
		}
		Ingredient ingredient = ingredientOptional.get();
		ingredient.setRecipe(null);
		recipe.getIngredients().remove(ingredient);
		recipeRepository.save(recipe);
	}
}
