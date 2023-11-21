package by.vlad.elibrary.service;

import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ClientService extends UserDetailsService {

    String createNewClient(UserRegisterDataRequestDto dto);

    Boolean authorizeClient(UserLoginDataRequestDto dto);
}
