package guru.springframework.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
	void saveImageFile(String id, MultipartFile multipartFile) throws IOException;
}
