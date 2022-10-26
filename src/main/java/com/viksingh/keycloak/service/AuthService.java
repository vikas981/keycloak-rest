package com.viksingh.keycloak.service;

import com.viksingh.keycloak.config.KeycloakClientConfig;
import com.viksingh.keycloak.dto.request.LoginRequestDTO;
import com.viksingh.keycloak.dto.request.ResetPasswordDTO;
import com.viksingh.keycloak.dto.request.UserRequestDTO;
import com.viksingh.keycloak.dto.response.LoginResponse;
import com.viksingh.keycloak.exception.wrapper.APIException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final Keycloak keycloak;
    private final KeycloakClientConfig keycloakClientConfig;

    public AuthService(Keycloak keycloak, KeycloakClientConfig keycloakClientConfig) {
        this.keycloak = keycloak;
        this.keycloakClientConfig = keycloakClientConfig;
    }

    public void createUser(UserRequestDTO userRequestDTO) {
        UsersResource userResource = keycloakClientConfig.getUserResource();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userRequestDTO.getUsername());
        userRepresentation.setFirstName(userRequestDTO.getFirstName());
        userRepresentation.setLastName(userRequestDTO.getLastName());
        userRepresentation.setEmail(userRequestDTO.getEmail());
        userRepresentation.setEmailVerified(false);
        RealmResource realmResource = keycloak.realm(keycloakClientConfig.getRealm());
        try (Response response = userResource.create(userRepresentation)) {
            log.info("Response |  Status: {} | Status Info: {}", response.getStatus(), response.getStatusInfo());
            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("User created with userId : {}", userId);
                CredentialRepresentation passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                passwordCred.setValue(userRequestDTO.getPassword());
                UserResource user = userResource.get(userId);
                // Set password credential
                user.resetPassword(passwordCred);
                RoleRepresentation realmRoleUser = realmResource.roles().get("app-user").toRepresentation();
                user.roles().realmLevel().add(Collections.singletonList(realmRoleUser));
            } else if (response.getStatus() == 409) {
                throw new APIException(HttpStatus.CONFLICT, "CONFLICT", "User already registered");
            }
        }
    }


    public LoginResponse login(LoginRequestDTO request) {
        try {
            Map<String, Object> clientCredentials = new HashMap<>();
            clientCredentials.put("secret", keycloakClientConfig.getSecretKey());
            clientCredentials.put("grant_type", "password");
            Configuration configuration =
                    new Configuration(keycloakClientConfig.getAuthUrl(), keycloakClientConfig.getRealm(),
                            keycloakClientConfig.getClientId(), clientCredentials, null);
            AuthzClient authzClient = AuthzClient.create(configuration);
            AccessTokenResponse response =
                    authzClient.obtainAccessToken(request.getUsername(), request.getPassword());
            LoginResponse.LoginResponseBuilder loginResponse = LoginResponse.builder();
            loginResponse.accessToken(response.getToken());
            loginResponse.expiresIn(response.getExpiresIn());
            loginResponse.refreshExpiresIn(response.getRefreshExpiresIn());
            loginResponse.refreshToken(response.getRefreshToken());
            loginResponse.tokenType(response.getTokenType());
            loginResponse.sessionState(response.getSessionState());
            loginResponse.scope(response.getScope()).build();
            return loginResponse.build();
        }catch (Exception e){
            log.error("Invalid request body",e);
            throw new APIException(HttpStatus.BAD_REQUEST,"400","Bad Request");
        }
    }

    public void updateUser(UserRequestDTO userRequestDTO) {
        UsersResource usersResource = keycloakClientConfig.getUserResource();
        try{
            List<UserRepresentation> userRepresentations = keycloak.realm(keycloakClientConfig.getRealm())
                    .users().search(userRequestDTO.getUsername(),true);
            int size = userRepresentations.size();
            if(size > 0){
                UserRepresentation userRepresentation = userRepresentations.get(0);
                userRepresentation.setEmail(userRequestDTO.getEmail());
                userRepresentation.setFirstName(userRequestDTO.getFirstName());
                userRepresentation.setLastName(userRequestDTO.getLastName());
                usersResource.get(userRepresentation.getId()).update(userRepresentation);
            }
        }catch (Exception e){
            log.error("Exception ",e);
        }
    }

    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        try{
            UsersResource userResource = keycloakClientConfig.getUserResource();
            UserRepresentation userRepresentation = keycloak.realm(keycloakClientConfig.getRealm())
                    .users().search(resetPasswordDTO.getUsername(), true)
                    .stream().findFirst().orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Not Found", "User doesn't exists"));
            userResource.get(userRepresentation.getId())
                    .executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
        }catch (Exception e) {
            log.error("Unable to send reset password mail",e);
        }

    }
}
