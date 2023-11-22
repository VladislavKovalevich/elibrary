package by.vlad.elibrary.repository;

import by.vlad.elibrary.model.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ClientRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @BeforeEach
    public void setUp(){
        testClient = Client.builder()
                .name("Name")
                .surname("Surname")
                .email("email1test@gmail.com")
                .password("Password")
                .isNonLocked(true)
                .build();
    }

    @Test
    public void countByEmailTest(){
        Integer countBefore = clientRepository.countByEmail(testClient.getEmail());

        clientRepository.save(testClient);

        Integer countAfter = clientRepository.countByEmail(testClient.getEmail());

        assertThat(countBefore).isEqualTo(0);
        assertThat(countAfter).isEqualTo(1);
    }

    @Test
    public void findByEmailTest(){
        Optional<Client> optionalClientBefore = clientRepository.findByEmail(testClient.getEmail());

        clientRepository.save(testClient);

        Optional<Client> optionalClientAfter = clientRepository.findByEmail(testClient.getEmail());

        assertThat(optionalClientBefore).isEmpty();
        assertThat(optionalClientAfter).isPresent();
        assertThat(optionalClientAfter.get().getEmail()).isEqualTo(testClient.getEmail());
    }

    @Test
    public void findClientsByIsNonLockedTest(){
        testClient.setIsNonLocked(false);
        clientRepository.save(testClient);

        List<Client> clientListBefore = clientRepository.findClientsByIsNonLocked(testClient.getIsNonLocked());

        testClient.setIsNonLocked(true);
        clientRepository.save(testClient);

        List<Client> clientListAfter = clientRepository.findClientsByIsNonLocked(testClient.getIsNonLocked());

        assertThat(clientListBefore).isNotEmpty();
        assertThat(clientListAfter).hasSize(1);
    }
}
