package by.vlad.elibrary.controller.impl;

import by.vlad.elibrary.config.security.JwtService;
import by.vlad.elibrary.controller.ClientManagementController;
import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientManagementControllerImpl implements ClientManagementController {

    private final ClientService clientService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<String> registerClient(UserRegisterDataRequestDto dto) {
        return new ResponseEntity<>(clientService.createNewClient(dto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> loginClient(UserLoginDataRequestDto dto) {

        boolean isAuthorized = clientService.authorizeClient(dto);

        ResponseEntity<Void> responseEntity;

        if (isAuthorized){
            String token = jwtService.generateToken(clientService.loadUserByUsername(dto.getEmail()));

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccessControlExposeHeaders(List.of("Authorization"));

            responseEntity = new ResponseEntity<>(headers, HttpStatus.OK);
        }else{
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }


    /*
    * Change user parameters
    *
    * */

    /*
    * Endpoint for secret code verification
    *
    * */
}
