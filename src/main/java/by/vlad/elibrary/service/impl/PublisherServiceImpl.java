package by.vlad.elibrary.service.impl;

import by.vlad.elibrary.exception.InvalidRequestDataException;
import by.vlad.elibrary.mapper.PublisherMapper;
import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
import by.vlad.elibrary.model.entity.Publisher;
import by.vlad.elibrary.repository.PublisherRepository;
import by.vlad.elibrary.service.BookComponentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static by.vlad.elibrary.exception.util.ExceptionMessage.PUBLISHER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements BookComponentsService<PublisherResponseDto, PublisherRequestDto> {

    private final PublisherRepository publisherRepository;

    private final PublisherMapper publisherMapper;

    @Override
    public PublisherResponseDto returnComponentById(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(()-> new InvalidRequestDataException(PUBLISHER_NOT_FOUND));

        return publisherMapper.fromEntityToDto(publisher);
    }

    @Override
    public List<PublisherResponseDto> returnComponentList() {
        List<Publisher> publishers = publisherRepository.findAll();
        return publisherMapper.fromEntitiesToDtos(publishers);
    }

    @Override
    public PublisherResponseDto createNewComponent(PublisherRequestDto dto) {
        Publisher publisher = publisherMapper.fromDtoToEntity(dto);
        publisher.setId(null);

        Publisher savedPublisher = publisherRepository.save(publisher);

        return publisherMapper.fromEntityToDto(savedPublisher);
    }

    @Override
    public PublisherResponseDto updateComponent(PublisherRequestDto dto) {
        if (!publisherRepository.existsById(dto.getId())){
            throw new InvalidRequestDataException(PUBLISHER_NOT_FOUND);
        }

        Publisher publisher = publisherMapper.fromDtoToEntity(dto);

        Publisher updatedPublisher = publisherRepository.save(publisher);

        return publisherMapper.fromEntityToDto(updatedPublisher);
    }
}
