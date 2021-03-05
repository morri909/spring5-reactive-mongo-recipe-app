package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IngredientControllerTest {

	@Mock
	IngredientService ingredientService;
	@Mock
	RecipeService recipeService;
	@Mock
	UnitOfMeasureService unitOfMeasureService;

	IngredientController sut;

	MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sut = new IngredientController(ingredientService, recipeService, unitOfMeasureService);
		mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
	}

	@Test
	public void list() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		Mockito.when(recipeService.findCommandById(Mockito.anyString())).thenReturn(recipeCommand);

		mockMvc.perform(get("/recipe/1/ingredients"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/ingredient/list"))
				.andExpect(model().attributeExists("recipe"));

		Mockito.verify(recipeService).findCommandById(Mockito.anyString());
	}

	@Test
	public void show() throws Exception {
		IngredientCommand ingredientCommand = new IngredientCommand();

		Mockito.when(ingredientService.findByRecipeIdAndId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(ingredientCommand);

		mockMvc.perform(get("/recipe/1/ingredient/2/show"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/ingredient/show"))
				.andExpect(model().attributeExists("ingredient"));

		Mockito.verify(ingredientService).findByRecipeIdAndId(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void newForm() throws Exception {
		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");

		Mockito.when(recipeService.findCommandById(Mockito.anyString())).thenReturn(recipeCommand);
		Mockito.when(unitOfMeasureService.listAll()).thenReturn(new HashSet<>());

		mockMvc.perform(get("/recipe/1/ingredient/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/ingredient/ingredientform"))
				.andExpect(model().attributeExists("ingredient"))
				.andExpect(model().attributeExists("uomList"));

		Mockito.verify(recipeService).findCommandById(Mockito.anyString());
	}

	@Test
	public void update() throws Exception {
		// given
		Mockito.when(ingredientService.findByRecipeIdAndId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new IngredientCommand());
		Set<UnitOfMeasureCommand> unitOfMeasures = new HashSet<>();
		unitOfMeasures.add(new UnitOfMeasureCommand());
		Mockito.when(unitOfMeasureService.listAll()).thenReturn(unitOfMeasures);

		mockMvc.perform(get("/recipe/1/ingredient/2/update"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/ingredient/ingredientform"))
				.andExpect(model().attributeExists("ingredient"))
				.andExpect(model().attributeExists("uomList"));
	}

	@Test
	public void save() throws Exception {
		// given
		IngredientCommand ingredientCommand = new IngredientCommand();

		IngredientCommand savedIngredientCommand = new IngredientCommand();
		savedIngredientCommand.setId("1");
		savedIngredientCommand.setRecipeId("2");
		Mockito.when(ingredientService.saveIngredientCommand(Mockito.any(IngredientCommand.class)))
				.thenReturn(savedIngredientCommand);

		// then
		String redirectUrl = "/recipe/" + savedIngredientCommand.getRecipeId() +
				"/ingredient/" + savedIngredientCommand.getId() + "/show";
		mockMvc.perform(post("/recipe/1/ingredient", ingredientCommand))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:" + redirectUrl));
		Mockito.verify(ingredientService).saveIngredientCommand(Mockito.any(IngredientCommand.class));
	}

	@Test
	public void delete() throws Exception {
		mockMvc.perform(get("/recipe/1/ingredient/2/delete"))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:/recipe/1/ingredients"));
		Mockito.verify(ingredientService).deleteById(Mockito.anyString(), Mockito.anyString());
	}
}