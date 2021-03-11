package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import reactor.core.publisher.Mono;

public interface IngredientService {
	Mono<IngredientCommand> findByRecipeIdAndId(String recipeId, String id);
	Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand);
	Mono<Void> deleteById(String recipeId, String id);
}
