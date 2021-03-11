package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

	private final IngredientToIngredientCommand ingredientToIngredientCommand;
	private final IngredientCommandToIngredient ingredientCommandToIngredient;
	private final RecipeReactiveRepository recipeReactiveRepository;
	private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;

	public IngredientServiceImpl(
			IngredientToIngredientCommand ingredientToIngredientCommand,
			IngredientCommandToIngredient ingredientCommandToIngredient,
			RecipeReactiveRepository recipeReactiveRepository,
			UnitOfMeasureReactiveRepository unitOfMeasureRepository
	) {
		this.ingredientToIngredientCommand = ingredientToIngredientCommand;
		this.ingredientCommandToIngredient = ingredientCommandToIngredient;
		this.recipeReactiveRepository = recipeReactiveRepository;
		this.unitOfMeasureRepository = unitOfMeasureRepository;
	}

	@Override
	public Mono<IngredientCommand> findByRecipeIdAndId(String recipeId, String id) {
		return recipeReactiveRepository
				.findById(recipeId)
				.flatMapIterable(Recipe::getIngredients)
				.filter(ingredient -> ingredient.getId().equals(id))
				.single()
				.map(ingredientToIngredientCommand::convert)
				.doOnNext(ingredientCommand -> ingredientCommand.setRecipeId(recipeId));
	}

	@Override
	public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
		Recipe recipe = recipeReactiveRepository.findById(command.getRecipeId()).block();

		if (recipe == null) {
			log.error("Recipe not found for id: " + command.getRecipeId());
			return Mono.just(new IngredientCommand());
		} else {
			Optional<Ingredient> ingredientOptional = recipe
					.getIngredients()
					.stream()
					.filter(ingredient -> ingredient.getId().equals(command.getId()))
					.findFirst();

			if (ingredientOptional.isPresent()) {
				Ingredient ingredientFound = ingredientOptional.get();
				ingredientFound.setDescription(command.getDescription());
				ingredientFound.setAmount(command.getAmount());
				ingredientFound.setUnitOfMeasure(unitOfMeasureRepository
						.findById(command.getUnitOfMeasure().getId()).block());

				if (ingredientFound.getUnitOfMeasure() == null) {
					new RuntimeException("UOM NOT FOUND");
				}
			} else {
				//add new Ingredient
				Ingredient ingredient = ingredientCommandToIngredient.convert(command);
				recipe.addIngredient(ingredient);
			}

			Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

			Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
					.filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
					.findFirst();

			//check by description
			if (!savedIngredientOptional.isPresent()) {
				//not totally safe... But best guess
				savedIngredientOptional = savedRecipe.getIngredients().stream()
						.filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
						.filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
						.filter(recipeIngredients -> recipeIngredients.getUnitOfMeasure().getId().equals(command.getUnitOfMeasure().getId()))
						.findFirst();
			}

			IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
			ingredientCommandSaved.setRecipeId(recipe.getId());

			return Mono.just(ingredientCommandSaved);
		}
	}

	@Override
	public Mono<Void> deleteById(String recipeId, String id) {
		log.debug("Deleting ingredient: " + recipeId + ":" + id);
		Recipe recipe = recipeReactiveRepository.findById(recipeId).block();

		if (recipe != null){
			Optional<Ingredient> ingredientOptional = recipe
					.getIngredients()
					.stream()
					.filter(ingredient -> ingredient.getId().equals(id))
					.findFirst();

			if(ingredientOptional.isPresent()){
				log.debug("found Ingredient");
				recipe.getIngredients().remove(ingredientOptional.get());
				recipeReactiveRepository.save(recipe).block();
			}
		} else {
			log.debug("Recipe Id Not found. Id:" + recipeId);
		}
		return Mono.empty();
	}
}
