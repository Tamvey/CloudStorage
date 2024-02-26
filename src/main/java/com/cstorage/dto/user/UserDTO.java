package com.cstorage.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
	private String username;
	private String password;
	private String authority;
	private int enabled;
	private String email;
}
