package com.viksingh.keycloak.service;

import com.viksingh.keycloak.config.KeycloakClientConfig;
import com.viksingh.keycloak.exception.wrapper.RoleNotFoundException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleService {

    private final Keycloak keycloak;
    private final KeycloakClientConfig keycloakClientConfig;

    public RoleService(Keycloak keycloak, KeycloakClientConfig keycloakClientConfig) {
        this.keycloak = keycloak;
        this.keycloakClientConfig = keycloakClientConfig;
    }

    public List<RoleRepresentation> getAllRoles() {
        return keycloak.realm(keycloakClientConfig.getRealm())
                .roles().list();
    }

    public RoleRepresentation getRoleByName(String roleName) {
        try{
            return keycloak.realm(keycloakClientConfig.getRealm())
                    .roles().get(roleName).toRepresentation();
        }catch(Exception e){
          throw new RoleNotFoundException(roleName+" doesn't exists!", HttpStatus.NOT_FOUND);
        }
    }

    public void createRole(Map<String,String> request,Map<String,String> response){
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(request.get("name"));
        roleRepresentation.setContainerId(keycloakClientConfig.getRealm());
        try{
            keycloak.realm(keycloakClientConfig.getRealm())
                    .roles().create(roleRepresentation);
            response.put("message","role created successfully");
            response.put("status", HttpStatus.OK.getReasonPhrase());
        }catch (ClientErrorException cee){
            if(cee.getResponse().getStatus()==409){
                response.put("message","role already exists");
                response.put("status", HttpStatus.CONFLICT.getReasonPhrase());
            }else {
                response.put("message","role not created");
                response.put("status", HttpStatus.BAD_REQUEST.getReasonPhrase());
            }
        }
    }
}
