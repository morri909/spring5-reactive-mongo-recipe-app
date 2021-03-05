package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {
	IngredientCommand findByRecipeIdAndId(String recipeId, String id);
	IngredientCommand saveIngredientCommand(IngredientCommand ingredientCommand);
	void deleteById(String recipeId, String id);
}
