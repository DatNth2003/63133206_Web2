package com.ntd63133206.bookbuddy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.RoleRepository;
import com.ntd63133206.bookbuddy.repository.UserRepository;
import com.ntd63133206.bookbuddy.util.Utility;


@Service
public class UserService {
    private final String uploadDir = "src/main/resources/static";

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	public void createDefaultUserAndRole() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User existingUser = userRepository.findByEmail("dat.nth.63cntt@ntu.edu.vn");
        if (existingUser == null) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("dat.nth.63cntt@ntu.edu.vn");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);

            // Lưu người dùng
            userRepository.save(adminUser);

            Role adminRole = roleRepository.findFirstByName("ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ADMIN");

                roleRepository.save(adminRole);
            }

            adminUser.getRoles().add(adminRole);
            userRepository.save(adminUser);
        }
    }
	public void assignRoleToUser(User user, Role newRole) {
	    Role existingRole = roleRepository.findFirstByName(newRole.getName());
	    if (existingRole != null && !user.getRoles().contains(existingRole)) {
	        user.getRoles().add(existingRole);
	    } else {
	        System.out.println("Role không tồn tại hoặc đã được gán cho người dùng");
	    }
	}


   
    public boolean registerUser(User user, MultipartFile avatarFile) throws IOException {
    	if (user == null || Utility.isNullOrEmpty(user.getUsername()) || Utility.isNullOrEmpty(user.getEmail())) {
    	    System.out.println("Thông tin đăng ký không đầy đủ.");
    	    return false;
    	}


        if (userRepository.existsByEmail(user.getEmail())) {
            System.out.println("Đã tồn tại email: " + user.getEmail());
            return false;
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            System.out.println("Tên người dùng đã tồn tại: " + user.getUsername());
            return false;
        }

        Role roleUser = roleRepository.findFirstByName("USER");
        if (roleUser == null) {
            roleUser = new Role();
            roleUser.setName("USER");
            roleRepository.save(roleUser);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRoles(roles);

        userRepository.save(user);

        return true;
    }


    public boolean authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        System.out.println("Kích hoạt service authenticateUser cho " + email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("So sánh: " + passwordEncoder.encode(password) + " và " + user.getPassword());
            System.out.println("Authentication done for email: " + email);
            LocalDateTime currentDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(currentDateTime);
            user.setLastLogin(timestamp);
            userRepository.save(user);

            return true;
        }
        System.out.println("Authentication failed for email: " + email);
        return false;
    }

    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        System.out.println("Email có tồn tại: " + email + ", Result: " + exists);
        return exists;
    }

    public boolean existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        System.out.println("Username có tồn tại: " + username + ", Result: " + exists);
        return exists;
    }

    public User getUserByEmail(String email) {
    	
        return userRepository.findByEmail(email);
    }

    public static String generateResePasswordtToken(String email) {
        return UUID.randomUUID().toString();
    }

    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetPasswordToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void updateUsername(String email, String newUsername) {
        if (existsByEmail(email)) {
            User user = userRepository.findByEmail(email);
            user.setUsername(newUsername);
            userRepository.save(user);
            System.out.println("updateUsername: Đã cập nhật tên " + newUsername);
        }
    }

    public void updateAvatar(User user, MultipartFile avatarFile) throws IOException {
        if (user != null) {
            String oldAvatarFileName = user.getAvatar();
            if (oldAvatarFileName != null && !oldAvatarFileName.isEmpty()) {
                deleteAvatar(oldAvatarFileName);
            }

            String avatarFileName = saveAvatar(avatarFile);

            user.setAvatar(avatarFileName);
            userRepository.save(user);
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
                User user = userRepository.findByEmail(email);
                updateAvatar(user, avatarFile);
            } else {
                throw new IllegalArgumentException("User with email " + email + " does not exist");
            }
        }
    }


    public Optional<User> getUserById(Long id) {
        System.out.println("Đã lấy user!");
        return userRepository.findById(id);
    }

    public void save(User user) {
        System.out.println("Đã lưu user " + user.getEmail() + " vào csdl!");
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        String uploadsDir = uploadDir + "/images/users/avatars/";

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Path avatarPath = Paths.get(uploadsDir + user.getAvatar());
                try {
                    Files.deleteIfExists(avatarPath);
                    System.out.println("Deleted avatar for user: " + user.getEmail());
                } catch (IOException e) {
                    System.out.println("Error deleting avatar for user " + user.getEmail() + ": " + e.getMessage());
                }
            }

            userRepository.deleteById(id);
            System.out.println("Đã xóa người dùng : " + id);
        } else {
            System.out.println("Người dùng " + id + " không tồn tại.");
        }
    }


    public Page<User> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        
        if(usersPage.isEmpty()) {
            System.out.println("Không có người dùng nào.");
        }
        System.out.println("Đã lấy người dùng thành công!");
        return usersPage;
    }
    public List<User> getAllUsers() {
        System.out.println("Đã lấy tất cả user!");
        return userRepository.findAll();
    }
    public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("Could not find any user with the email " + email);
        }
    }

    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public Page<User> searchUsersByKeywordAndRole(String keyword, String role, Pageable pageable) {
        if (keyword != null && role != null) {
            return userRepository.findByKeywordAndRole(keyword, role, pageable);
        } else if (keyword != null) {
            return userRepository.findByKeyword(keyword, pageable);
        } else if (role != null) {
            return userRepository.findByRole(role, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }



}
