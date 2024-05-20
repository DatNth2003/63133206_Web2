package com.ntd63133206.bookbuddy.service;

import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.model.User;
import com.ntd63133206.bookbuddy.repository.RoleRepository;
import com.ntd63133206.bookbuddy.repository.UserRepository;
import com.ntd63133206.bookbuddy.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

	private static final String UPLOAD_DIR = "src/main/resources/static/images/users/avatars/";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createDefaultUserAndRole() {
        User existingUser = userRepository.findByEmail("dat.nth.63cntt@ntu.edu.vn");
        if (existingUser == null) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("dat.nth.63cntt@ntu.edu.vn");
            adminUser.setPassword(passwordEncoder.encode("Admin@1234"));
            adminUser.setAvatar("default/default-avatar.jpg");
            adminUser.setEnabled(true);
            
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);
            userRepository.save(adminUser);
        }
    }


    public void assignRoleToUser(User user, Role newRole) {
        Role existingRole = roleRepository.findByName(newRole.getName());
        if (existingRole != null && !user.getRoles().contains(existingRole)) {
            user.getRoles().add(existingRole);
            userRepository.save(user);
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

        Role roleUser = roleRepository.findByName("ROLE_USER");
        if (roleUser == null) {
            roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setRoles(roles);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String avatarFileName = saveAvatar(avatarFile);
            user.setAvatar(avatarFileName);
        }

        userRepository.save(user);
        return true;
    }

    public boolean authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(currentDateTime);
            user.setLastLogin(timestamp);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public static String generateResetPasswordToken() {
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

    public void updateAvatar(User user, MultipartFile avatarFile) throws IOException {
        if (user != null) {
            String oldAvatarFileName = user.getAvatar();
            if (oldAvatarFileName != null && !oldAvatarFileName.isEmpty()) {
                deleteAvatar(oldAvatarFileName);
            }
            String avatarFileName = saveAvatar(avatarFile);
            user.setAvatar(avatarFileName);
            System.out.println("Đã lưu: " + avatarFileName  + " cho " + user.getEmail());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User cannot be null");
        }
    }

    private void deleteAvatar(String avatarFileName) {
        try {
            Path path = Paths.get(UPLOAD_DIR, avatarFileName);
            Files.deleteIfExists(path);
            System.out.println("Deleted old avatar: " + avatarFileName);
        } catch (IOException e) {
            System.out.println("Error deleting old avatar: " + e.getMessage());
        }
    }

    public String saveAvatar(MultipartFile avatarFile) {
        try {
            if (avatarFile.isEmpty()) {
                throw new IllegalArgumentException("Avatar file is empty");
            }
            
            System.out.println("Đang tải ảnh: " + avatarFile.getOriginalFilename());

            String originalFileName = avatarFile.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + fileExtension; // Tên file với UUID + đuôi mở rộng

            Path directory = Paths.get(UPLOAD_DIR);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                System.out.println("Created directory: " + directory.toString());
            }

            Path path = directory.resolve(filename);
            Files.copy(avatarFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved Avatar: " + path.toString());

            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void updateUsername(String oldUsername, String newUsername) {
        User user = userRepository.findByUsername(oldUsername);
        if (user != null) {
            user.setUsername(newUsername);
            userRepository.save(user);
            System.out.println("Updated username to: " + newUsername);
        }
    }

    public void updateProfile(String oldUsername, String newUsername, MultipartFile avatarFile) throws IOException {
        System.out.println("Updating profile for username: " + oldUsername + ", new username: " + newUsername);

        updateUsername(oldUsername, newUsername);

        if (!avatarFile.isEmpty()) {
            System.out.println("Avatar file is not empty for username: " + oldUsername);
            User user = userRepository.findByUsername(newUsername);
            if (user != null) {
                System.out.println("User with username exists: " + newUsername);

                updateAvatar(user, avatarFile);
            } else {
                System.out.println("User with username does not exist: " + newUsername);
                throw new IllegalArgumentException("User with username " + newUsername + " does not exist");
            }
        } else {
            System.out.println("Avatar file is empty for username: " + oldUsername);
        }
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        System.out.println("Saved user " + user.getEmail());
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                deleteAvatar(user.getAvatar());
            }
            userRepository.deleteById(id);
            System.out.println("Deleted user: " + id);
        } else {
            System.out.println("User " + id + " does not exist.");
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<User> getAllUsers() {
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
        user.setPassword(passwordEncoder.encode(newPassword));
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
