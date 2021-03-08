package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerTest {

	@Mock
	RecipeService recipeService;

	RecipeController sut;

	MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		sut = new RecipeController(recipeService);
		mockMvc = MockMvcBuilders.standaloneSetup(sut)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@Test
	public void showById() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId("1");
		Mockito.when(recipeService.findById(Mockito.anyString())).thenReturn(Mono.just(recipe));

		mockMvc.perform(get("/recipe/" + recipe.getId() + "/show"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/show"))
				.andExpect(model().attributeExists("recipe"));
	}

	@Test
	public void newRecipe() throws Exception {
		mockMvc.perform(get("/recipe/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/recipeform"))
				.andExpect(model().attributeExists("recipe"));
	}

	@Test
	public void updateRecipe() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");

		Mockito.when(recipeService.findCommandById(Mockito.anyString())).thenReturn(Mono.just(recipeCommand));

		mockMvc.perform(get("/recipe/" + recipeCommand.getId() + "/update"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/recipeform"))
				.andExpect(model().attributeExists("recipe"));
	}

	@Test
	public void save() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");
		recipeCommand.setDescription("test");
		recipeCommand.setDirections("this is a test");

		Mockito.when(recipeService.saveRecipeCommand(Mockito.any(RecipeCommand.class)))
				.thenReturn(Mono.just(recipeCommand));

		mockMvc.perform(post("/recipe")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("id", recipeCommand.getId().toString())
					.param("description", recipeCommand.getDescription())
					.param("directions", recipeCommand.getDirections())
				)
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/recipe/"  + recipeCommand.getId() + "/show"));

		Mockito.verify(recipeService).saveRecipeCommand(Mockito.any(RecipeCommand.class));
	}

	@Test
	public void saveValidationFail() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");

		Mockito.when(recipeService.saveRecipeCommand(Mockito.any(RecipeCommand.class)))
				.thenReturn(Mono.just(recipeCommand));

		mockMvc.perform(post("/recipe", recipeCommand))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/recipeform"));

		Mockito.verify(recipeService, Mockito.never()).saveRecipeCommand(Mockito.any(RecipeCommand.class));
	}

	@Test
	public void delete() throws Exception {
		mockMvc.perform(get("/recipe/1/delete"))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/"));

		Mockito.verify(recipeService).deleteById(Mockito.anyString());
	}

	@Test
	public void getRecipeNotFound() throws Exception {
		Mockito.when(recipeService.findById(Mockito.anyString())).thenThrow(NotFoundException.class);

		mockMvc.perform(get("/recipe/1/show"))
				.andExpect(status().isNotFound())
				.andExpect(view().name("404error"));
	}

}