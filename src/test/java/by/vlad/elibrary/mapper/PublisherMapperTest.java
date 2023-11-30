package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
import by.vlad.elibrary.model.entity.Publisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PublisherMapperTest {


    @InjectMocks
    private PublisherMapperImpl publisherMapper;

    private final Publisher expectedPublisher;

    private final PublisherRequestDto expectedPublisherRequestDto;

    private final PublisherResponseDto expectedPublisherResponseDto;

    private final List<Publisher> expectedPublishers;

    private final List<PublisherResponseDto> expectedPublisherResponseDtos;

    public PublisherMapperTest(){
        expectedPublisher = Publisher.builder()
                .id(1L)
                .name("NAME")
                .build();

        expectedPublisherResponseDto = PublisherResponseDto.builder()
                .name("NAME")
                .build();

        expectedPublisherRequestDto = PublisherRequestDto.builder()
                .id(1L)
                .name("NAME")
                .build();

        expectedPublishers = List.of(expectedPublisher);

        expectedPublisherResponseDtos = List.of(expectedPublisherResponseDto);
    }

    @Test
    public void fromDtoToEntityShouldReturnValidAuthor(){
        Publisher actualPublisher = publisherMapper.fromDtoToEntity(expectedPublisherRequestDto);

        assertThat(actualPublisher).isEqualTo(expectedPublisher);
    }

    @Test
    public void fromDtoToEntityShouldReturnNullIfDtoIsNull(){
        Publisher actualPublisher = publisherMapper.fromDtoToEntity(null);

        assertThat(actualPublisher).isNull();
    }

    @Test
    public void fromEntityToDtoShouldReturnValidDto(){
        PublisherResponseDto actualPublisherResponseDto = publisherMapper.fromEntityToDto(expectedPublisher);

        assertThat(actualPublisherResponseDto).isEqualTo(expectedPublisherResponseDto);
    }

    @Test
    public void fromEntityToDtoShouldReturnNullIfEntityIsNull(){
        PublisherResponseDto actualPublisherResponseDto = publisherMapper.fromEntityToDto(null);

        assertThat(actualPublisherResponseDto).isNull();
    }

    @Test
    public void fromEntitiesToDtosShouldReturnValidList(){
        List<PublisherResponseDto> actualPublisherResponseDtos = publisherMapper.fromEntitiesToDtos(expectedPublishers);

        assertThat(actualPublisherResponseDtos).isEqualTo(expectedPublisherResponseDtos);
    }

    @Test
    public void fromEntitiesToDtosShouldReturnEmptyListIfListIsNull(){
        List<PublisherResponseDto> actualPublisherResponseDtos = publisherMapper.fromEntitiesToDtos(null);

        assertThat(actualPublisherResponseDtos).isNull();
    }
}
