package com.ntd63133206.bookbuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ntd63133206.bookbuddy.model.Role;
import com.ntd63133206.bookbuddy.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
//        for (Role role : roles) {
//            System.out.println(role.getName());
//        }
        return roles;
    }


    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    public Role getRoleByName(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            throw new IllegalArgumentException("Role with name '" + name + "' not found.");
        }
        return role;
    }
    public Role updateRole(Long id, Role updatedRole) {
        Optional<Role> existingRoleOptional = roleRepository.findById(id);
        if (existingRoleOptional.isPresent()) {
            Role existingRole = existingRoleOptional.get();
            existingRole.setName(updatedRole.getName());
            return roleRepository.save(existingRole);
        } else {
            return null;
        }
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
