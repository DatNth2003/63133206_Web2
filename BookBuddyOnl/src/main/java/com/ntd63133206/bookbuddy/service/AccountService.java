	package com.ntd63133206.bookbuddy.service;
	
	import java.io.IOException;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.nio.file.StandardCopyOption;
	import java.sql.Timestamp;
	import java.time.LocalDateTime;
	import java.util.List;
	import java.util.Optional;
	import java.util.UUID;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.crypto.password.PasswordEncoder;
	import org.springframework.stereotype.Service;
	import org.springframework.web.multipart.MultipartFile;
	
	import com.ntd63133206.bookbuddy.model.User;
	import com.ntd63133206.bookbuddy.repository.AccountRepository;
	
	@Service
	public class AccountService {
		private final String uploadDir = "src/main/resources/static";
		@Autowired
	    private AccountRepository accountRepository;
	    
		@Autowired
	    private PasswordEncoder passwordEncoder;
	
	    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
	        this.accountRepository = accountRepository;
	        this.passwordEncoder = passwordEncoder;
	    }
	
	    public boolean registerUser(User user) {
	        if (accountRepository.existsByEmail(user.getEmail())) {
	            System.out.println("Đã tồn tại email: " + user.getEmail());
	            return false;
	        }
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        user.setRole("ROLE_USER");
	        user.setEnabled(true);
	        accountRepository.save(user);
	        return true;
	    }
	
	    public boolean authenticateUser(String email, String password) {
	        User user = accountRepository.findByEmail(email);
	        System.out.println("Kích hoạt service authenticateUser cho " + email);
	        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
	            System.out.println("So sánh: " + passwordEncoder.encode(password) + " và " + user.getPassword());
	            System.out.println("Authentication done for email: " + email);
	            LocalDateTime currentDateTime = LocalDateTime.now();
	            Timestamp timestamp = Timestamp.valueOf(currentDateTime);
	            user.setLastLogin(timestamp);
	            accountRepository.save(user);
	
	            return true;
	        }
	        System.out.println("Authentication failed for email: " + email);
	        return false;
	    }
	
	    public boolean existsByEmail(String email) {
	        boolean exists = accountRepository.existsByEmail(email);
	        System.out.println("Email có tồn tại: " + email + ", Result: " + exists);
	        return exists;
	    }
	    public boolean existsByUsername(String username) {
	        boolean exists = accountRepository.existsByUsername(username);
	        System.out.println("Username có tồn tại: " + username + ", Result: " + exists);
	        return exists;
	    }
	    public User getUserByEmail(String email) {
	        return accountRepository.findByEmail(email);
	    }
	
	    public static String generateResetToken(String email) {
	        
	        return UUID.randomUUID().toString();
	    }
	    
	    public boolean resetPassword(String token, String newPassword) {
	        User user = accountRepository.findByResetToken(token);
	        if (user != null) {
	            user.setPassword(passwordEncoder.encode(newPassword));
	            user.setResetToken(null);
	            accountRepository.save(user);
	            return true;
	        }
	        return false;
	    }
	    public void updateUsername(String email, String newUsername) {
	        if (existsByEmail(email)) {
	            User user = accountRepository.findByEmail(email);
	            user.setUsername(newUsername);
	            accountRepository.save(user);
	            System.out.println("updateUsername: Đã cập nhật tên " + newUsername);
	        }
	    }
	
	    public void updateAvatar(User user, MultipartFile avatarFile) throws IOException {
	        if (user != null) {
	            // Kiểm tra xem người dùng có avatar cũ không
	            String oldAvatarFileName = user.getAvatar();
	            if (oldAvatarFileName != null && !oldAvatarFileName.isEmpty()) {
	                deleteAvatar(oldAvatarFileName);
	            }

	            // Lưu tên ảnh mới
	            String avatarFileName = saveAvatar(avatarFile);

	            // Cập nhật tên ảnh mới cho người dùng
	            user.setAvatar(avatarFileName);
	            accountRepository.save(user);
	            System.out.println("Updated avatar path for user: " + user.getEmail());
	        } else {
	            throw new IllegalArgumentException("User cannot be null");
	        }
	    }

	    private void deleteAvatar(String avatarFileName) {
	        String avatarPath = uploadDir + "/images/users/avatars/" + avatarFileName;
	        Path path = Paths.get(avatarPath);
	        try {
	            // Xóa tệp ảnh cũ
	            Files.deleteIfExists(path);
	            System.out.println("Deleted old avatar: " + avatarFileName);
	        } catch (IOException e) {
	            System.out.println("Error deleting old avatar: " + e.getMessage());
	        }
	    }


	    public String saveAvatar(MultipartFile avatarFile) throws IOException {
	        if (avatarFile.isEmpty()) {
	            throw new IllegalArgumentException("Avatar file is empty");
	        }

	        String filename = UUID.randomUUID().toString() + "-" + avatarFile.getOriginalFilename();

	        try {
	            // Xác định đường dẫn đến thư mục để lưu ảnh
	            Path directory = Paths.get(uploadDir + "/images/users/avatars/");
	            System.out.println("Save Avatar: directory" + directory);

	            if (!Files.exists(directory)) {
	                Files.createDirectories(directory); // Tạo thư mục nếu chưa tồn tại
	            }

	            // Xác định đường dẫn đến tập tin ảnh mới
	            Path path = directory.resolve(filename);
	            System.out.println("Save Avatar: directory" + directory);

	            // Sao chép dữ liệu từ InputStream của avatarFile vào tập tin mới
	            Files.copy(avatarFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	            // Trả về tên file mới
	            return filename;
	        } catch (IOException e) {
	            System.out.println("Save Avatar: error" + e.getMessage());
	            throw new IOException("Error saving avatar: " + e.getMessage());
	        }
	    }


	
	
	
	
	
	    public void updateProfile(String email, String username, MultipartFile avatarFile) throws IOException {
	        updateUsername(email, username);
	        
	        if (!avatarFile.isEmpty()) {
	            if (existsByEmail(email)) {
	                User user = accountRepository.findByEmail(email);
	                updateAvatar(user, avatarFile);
	            } else {
	                throw new IllegalArgumentException("User with email " + email + " does not exist");
	            }
	        }
	    }
	
	
	
	
	
	    public List<User> getAllUsers() {
	        System.out.println("Đã lấy tất cả user!");
	        return accountRepository.findAll();
	    }
	
	    public Optional<User> getUserById(Long id) {
	    	System.out.println("Đã lấy user!");
	        return accountRepository.findById(id);
	    }
	
	    public void saveUser(User user) {
	    	System.out.println("Đã lưu user " + user.getEmail()  + " vào csdl!");
	        accountRepository.save(user);
	    }
	
	    public void deleteUserById(Long id) {
	        String uploadsDir = uploadDir + "/images/users/avatars/";
	
	        Optional<User> optionalUser = accountRepository.findById(id);
	        if (optionalUser.isPresent()) {
	            User user = optionalUser.get();
	            
	            // Kiểm tra xem người dùng có ảnh đại diện không
	            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
	                Path avatarPath = Paths.get(uploadsDir + user.getAvatar());
	                try {
	                    // Xóa ảnh đại diện
	                    Files.deleteIfExists(avatarPath);
	                    System.out.println("Deleted avatar for user: " + user.getEmail());
	                } catch (IOException e) {
	                    System.out.println("Error deleting avatar for user " + user.getEmail() + ": " + e.getMessage());
	                }
	            }
	            
	            accountRepository.deleteById(id);
	            System.out.println("Đã xóa người dùng : " + id);
	        } else {
	            System.out.println("Người dùng " + id + " không tồn tại.");
	        }
	    }
	
	    
	}
