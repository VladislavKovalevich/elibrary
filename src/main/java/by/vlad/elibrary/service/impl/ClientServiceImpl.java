package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.ClientMapper;
import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Role;
import by.vlad.elibrary.repository.ClientRepository;
import by.vlad.elibrary.service.ClientService;
import by.vlad.elibrary.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static by.vlad.elibrary.exception.util.ExceptionMessage.CLIENT_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.PASSWORDS_MISMATCH;
import static by.vlad.elibrary.exception.util.ExceptionMessage.USER_EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String createNewClient(UserRegisterDataRequestDto dto) {
        if (clientRepository.countByEmail(dto.getEmail()) != 0){
            throw new InvalidRequestDataException(USER_EMAIL_ALREADY_EXISTS);
        }

        if (!Objects.equals(dto.getPassword(), dto.getRepeatedPassword())){
            throw new InvalidRequestDataException(PASSWORDS_MISMATCH);
        }

        Client client = clientMapper.convertUserRegistrationDataRequestDtoToClientEntity(dto);

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Role.CLIENT);

        clientRepository.save(client);

        return "OK";
    }

    @Override
    public boolean authorizeClient(UserLoginDataRequestDto dto) {
        Client client = clientRepository.findByEmail(dto.getEmail()).orElseThrow(()->{
            throw new InvalidRequestDataException(CLIENT_NOT_FOUND);
        });

        return passwordEncoder.verifyPassword(dto.getPassword(), client.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByEmail(username).orElseThrow(()->{
           throw new InvalidRequestDataException(CLIENT_NOT_FOUND);
        });
    }
}
