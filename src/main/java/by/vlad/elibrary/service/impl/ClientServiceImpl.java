package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.ClientMapper;
import by.vlad.elibrary.model.dto.request.UserLoginDataRequestDto;
import by.vlad.elibrary.model.dto.request.UserRegisterDataRequestDto;
import by.vlad.elibrary.model.entity.Client;
import by.vlad.elibrary.model.entity.Order;
import by.vlad.elibrary.model.entity.OrderStatus;
import by.vlad.elibrary.model.entity.Role;
import by.vlad.elibrary.repository.ClientRepository;
import by.vlad.elibrary.repository.OrderRepository;
import by.vlad.elibrary.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static by.vlad.elibrary.exception.util.ExceptionMessage.CLIENT_NOT_FOUND;
import static by.vlad.elibrary.exception.util.ExceptionMessage.PASSWORDS_MISMATCH;
import static by.vlad.elibrary.exception.util.ExceptionMessage.USER_EMAIL_ALREADY_EXISTS;
import static by.vlad.elibrary.exception.util.ExceptionMessage.WRONG_CREDENTIALS;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final OrderRepository orderRepository;

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
        client.setIsNonLocked(true);

        clientRepository.save(client);

        return "OK";
    }

    @Override
    public Boolean authorizeClient(UserLoginDataRequestDto dto) {
        Client client = clientRepository.findByEmail(dto.getEmail()).orElseThrow(()->{
            throw new InvalidRequestDataException(WRONG_CREDENTIALS);
        });

        return passwordEncoder.matches(dto.getPassword(), client.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByEmail(username).orElseThrow(()->{
           throw new InvalidRequestDataException(CLIENT_NOT_FOUND);
        });
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkUserState() {
        List<Client> clientList =  clientRepository.findClientsByIsNonLocked(false);
        LocalDate date = LocalDate.now();

        for (Client c : clientList) {
            Optional<Order> optionalOrder = orderRepository
                    .findTopByClientIdAndStatusOrderByReturnedDateDesc(c.getId(), OrderStatus.OVERDUE);

            if (optionalOrder.isPresent()){
                Order order = optionalOrder.get();

                if (ChronoUnit.DAYS.between(order.getReturnedDate(), date) > 20){
                    c.setIsNonLocked(true);
                    clientRepository.save(c);
                }
            }
        }
    }

}
