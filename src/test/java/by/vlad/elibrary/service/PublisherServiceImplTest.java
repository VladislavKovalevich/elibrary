package by.vlad.elibrary.service;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.PublisherMapper;
import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
import by.vlad.elibrary.model.entity.Publisher;
import by.vlad.elibrary.repository.PublisherRepository;
import by.vlad.elibrary.service.impl.PublisherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static by.vlad.elibrary.exception.util.ExceptionMessage.PUBLISHER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceImplTest {

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private PublisherMapper publisherMapper;

    private final Publisher publisher;

    private final PublisherRequestDto publisherRequestDto;

    private final PublisherResponseDto publisherResponseDto;

    PublisherServiceImplTest() {
        publisher = Publisher.builder()
                .id(1L)
                .name("NAME")
                .address("ADDRESS")
                .build();

        publisherRequestDto = PublisherRequestDto.builder()
                .id(1L)
                .name("NAME")
                .address("ADDRESS")
                .build();

        publisherResponseDto = PublisherResponseDto.builder()
                .name("NAME")
                .address("ADDRESS")
                .build();
    }

    @Test
    public void returnPublisherByIdShouldReturnValidPublisher() {
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(publisherMapper.fromEntityToDto(publisher)).thenReturn(publisherResponseDto);

        PublisherResponseDto dtoResult = publisherService.returnComponentById(1L);

        assertThat(dtoResult.getName()).isEqualTo(publisherResponseDto.getName());
        assertThat(dtoResult.getAddress()).isEqualTo(publisherResponseDto.getAddress());

        verify(publisherRepository, times(1)).findById(1L);
        verify(publisherMapper, times(1)).fromEntityToDto(publisher);
    }

    @Test
    public void returnPublisherByIdShouldReturnErrorIfPublisherNotExists() {
        when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestDataException.class, () -> publisherService.returnComponentById(1L),
                PUBLISHER_NOT_FOUND);

        verify(publisherRepository, times(1)).findById(1L);
    }

    @Test
    public void returnAllPublishersShouldReturnValidList() {
        List<Publisher> publishers = List.of(publisher);
        List<PublisherResponseDto> dtos = List.of(publisherResponseDto);

        when(publisherRepository.findAll()).thenReturn(publishers);
        when(publisherMapper.fromEntitiesToDtos(publishers)).thenReturn(dtos);

        List<PublisherResponseDto> responseDtos = publisherService.returnComponentList();

        assertThat(responseDtos).hasSize(1);

        verify(publisherRepository, times(1)).findAll();
        verify(publisherMapper, times(1)).fromEntitiesToDtos(publishers);
    }

    @Test
    public void createNewPublisherShouldReturnValidPublisher() {
        when(publisherMapper.fromDtoToEntity(publisherRequestDto)).thenReturn(publisher);
        when(publisherRepository.save(publisher)).thenReturn(publisher);
        when(publisherMapper.fromEntityToDto(publisher)).thenReturn(publisherResponseDto);

        PublisherResponseDto dtoResult = publisherService.createNewComponent(publisherRequestDto);

        assertThat(dtoResult.getName()).isEqualTo(publisherRequestDto.getName());
        assertThat(dtoResult.getAddress()).isEqualTo(publisherRequestDto.getAddress());

        verify(publisherMapper, times(1)).fromDtoToEntity(publisherRequestDto);
        verify(publisherRepository, times(1)).save(publisher);
        verify(publisherMapper, times(1)).fromEntityToDto(publisher);
    }

    @Test
    public void updatePublisherShouldReturnValidPublisher() {

        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(publisherMapper.fromDtoToEntity(publisherRequestDto)).thenReturn(publisher);
        when(publisherRepository.save(publisher)).thenReturn(publisher);
        when(publisherMapper.fromEntityToDto(publisher)).thenReturn(publisherResponseDto);

        PublisherResponseDto dtoResult = publisherService.updateComponent(publisherRequestDto);

        assertThat(dtoResult.getName()).isEqualTo(publisherRequestDto.getName());
        assertThat(dtoResult.getAddress()).isEqualTo(publisherRequestDto.getAddress());

        verify(publisherRepository, times(1)).existsById(1L);
        verify(publisherMapper, times(1)).fromDtoToEntity(publisherRequestDto);
        verify(publisherRepository, times(1)).save(publisher);
        verify(publisherMapper, times(1)).fromEntityToDto(publisher);
    }

    @Test
    public void updatePublisherShouldReturnErrorIfPublisherNotExists() {

        when(publisherRepository.existsById(1L)).thenReturn(false);

        assertThrows(InvalidRequestDataException.class, () -> publisherService.updateComponent(publisherRequestDto),
                PUBLISHER_NOT_FOUND);

        verify(publisherRepository, times(1)).existsById(1L);
    }
}
