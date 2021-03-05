package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

	private final RecipeRepository recipeRepository;

	public ImageServiceImpl(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}

	@Override
	@Transactional
	public void saveImageFile(String id, MultipartFile multipartFile) throws IOException {
		log.debug("Saving image file: " + multipartFile.getOriginalFilename());
		Optional<Recipe> recipeOptional = recipeRepository.findById(id);
		if (!recipeOptional.isPresent()) {
			log.warn("Unable to find recipe: " + id);
			return;
		}
		Recipe recipe = recipeOptional.get();
		Byte[] bytes = new Byte[multipartFile.getBytes().length];
		for (int i = 0; i < multipartFile.getBytes().length; i++) {
			bytes[i] = multipartFile.getBytes()[i];
		}
		recipe.setImage(bytes);
		recipeRepository.save(recipe);
		log.debug("Saved image file: " + multipartFile.getOriginalFilename());
	}
}
