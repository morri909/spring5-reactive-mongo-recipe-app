package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public class ImageServiceImplTest {

	@Mock
	RecipeRepository recipeRepository;

	ImageServiceImpl sut;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		sut = new ImageServiceImpl(recipeRepository);
	}

	@Test
	public void saveImageFile() throws IOException {
		String id = "1";
		MultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain",
				"This is a test".getBytes());

		Recipe recipe = new Recipe();
		recipe.setId(id);
		Optional<Recipe> recipeOptional = Optional.of(recipe);

		Mockito.when(recipeRepository.findById(Mockito.anyString())).thenReturn(recipeOptional);

		ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);

		//when
		sut.saveImageFile(id, multipartFile);

		//then
		Mockito.verify(recipeRepository).save(argumentCaptor.capture());
		Recipe savedRecipe = argumentCaptor.getValue();
		Assert.assertEquals(multipartFile.getBytes().length, savedRecipe.getImage().length);
	}
}