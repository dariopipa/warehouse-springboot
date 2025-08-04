package io.github.dariopipa.warehouse.entities;

import io.github.dariopipa.warehouse.enums.RolesEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private RolesEnum role;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public RolesEnum getRole() {
		return role;
	}

	public void setRole(RolesEnum role) {
		this.role = role;
	}

	public Roles() {
	}

}
