package thiGK.ntu63133206.services;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageStorageService {
    
    private static final String UPLOAD_DIR = "src/main/resources/templates/images/";

    public String saveImage(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(new File(UPLOAD_DIR + fileName)));
            return "/images/" + fileName;
        }
        return null;
    }
}
