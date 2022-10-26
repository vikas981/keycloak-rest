package com.viksingh.keycloak.controller;

import com.viksingh.keycloak.service.RoleService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/role")
    @RolesAllowed("admin")
    public List<RoleRepresentation> getAllRoles(){
        List<RoleRepresentation> allRoles = roleService.getAllRoles();
        return allRoles;
    }

    @PostMapping("/role")
    @RolesAllowed("admin")
    public ResponseEntity<Map<String,String>> createRole(@RequestBody Map<String,String> request){
        Map<String,String> response = new HashMap<>();
        roleService.createRole(request,response);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/role/{name}")
    @RolesAllowed("admin")
    public RoleRepresentation getRoleByName(@PathVariable("name") String name){
        return roleService.getRoleByName(name);
    }

}
