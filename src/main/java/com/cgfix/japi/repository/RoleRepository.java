package com.cgfix.japi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgfix.japi.model.role.Role;
import com.cgfix.japi.model.role.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleName name);
}
