package com.ntd63133206.bookbuddy.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    
    @Transient
    private String passwordConfirm;

    @Column(name = "balance")
    private double balance = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @Column(name = "enabled")
    private boolean enabled;
    
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "last_login")
    private Timestamp lastLogin;


	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public double getBalance() {
        return balance;
    }

    public String getAvatar() {
        return avatar;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return "Giao dịch rút tiền thành công. Số dư còn:";
        } else {
            return "Số dư không đủ để thực hiện giao dịch.";
        }
    }

	public User(User user) {
	    this.id = user.getId();
	    this.email = user.getEmail();
	    this.username = user.getUsername();
	    this.password = user.getPassword();
	    this.passwordConfirm = user.getPasswordConfirm();
	    this.balance = user.getBalance();
	    this.roles = user.getRoles();
	    this.enabled = user.isEnabled();
	    this.resetPasswordToken = user.getResetPasswordToken();
	    this.avatar = user.getAvatar();
	    this.lastLogin = user.getLastLogin();
	}

	public User() {
	}

}