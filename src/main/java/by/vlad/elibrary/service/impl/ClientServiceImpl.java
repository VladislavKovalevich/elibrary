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

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final ClientMapper clientMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String createNewClient(UserRegisterDataRequestDto dto) {
        if (clientRepository.countByEmail(dto.getEmail()) != 0){
            throw new InvalidRequestDataException("User with this email is already exists");
        }

        if (!Objects.equals(dto.getPassword(), dto.getRepeatedPassword())){
            throw new InvalidRequestDataException("Passwords must be identical");
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
            throw new InvalidRequestDataException("User with this username is not exists");
        });

        return passwordEncoder.verifyPassword(dto.getPassword(), client.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByEmail(username).orElseThrow(()->{
           throw new InvalidRequestDataException("User with this username is not exists");
        });
    }
}
