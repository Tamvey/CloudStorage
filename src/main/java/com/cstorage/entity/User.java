package com.cstorage.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="users")
@Data
@NoArgsConstructor
public class User {
	@Id
	private String username;

	private String password;

	private int enabled;

	private String authority;

	private String email;

	private String code;
}
