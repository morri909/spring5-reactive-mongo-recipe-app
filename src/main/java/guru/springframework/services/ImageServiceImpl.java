package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

	private final RecipeReactiveRepository recipeReactiveRepository;

	public ImageServiceImpl(RecipeReactiveRepository recipeReactiveRepository) {
		this.recipeReactiveRepository = recipeReactiveRepository;
	}

	@Override
	@Transactional
	public Mono<Void> saveImageFile(String id, MultipartFile multipartFile) throws IOException {
		log.debug("Saving image file: " + multipartFile.getOriginalFilename());
		return recipeReactiveRepository.findById(id)
				.map(recipe -> {
					try {
						Byte[] bytes = new Byte[multipartFile.getBytes().length];
						for (int i = 0; i < multipartFile.getBytes().length; i++) {
							bytes[i] = multipartFile.getBytes()[i];
						}
						recipe.setImage(bytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return recipe;
				})
				.map(recipe -> recipeReactiveRepository.save(recipe))
				.then();
	}
}
