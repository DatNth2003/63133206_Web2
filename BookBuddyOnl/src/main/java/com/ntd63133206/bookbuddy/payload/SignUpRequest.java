package com.ntd63133206.bookbuddy.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignUpRequest {
	 @NotBlank
	    @Size(min = 4, max = 40)
	    private String name;

	    @NotBlank
	    @Size(max = 40)
	    @Email
	    private String email;

	    @NotBlank
	    @Size(min = 6, max = 20)
	    private String password;
}
