package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.PublisherRequestDto;
import by.vlad.elibrary.model.dto.response.PublisherResponseDto;
import by.vlad.elibrary.model.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublisherMapper {

    Publisher fromDtoToEntity(PublisherRequestDto dto);

    PublisherResponseDto fromEntityToDto(Publisher publisher);

    List<PublisherResponseDto> fromEntitiesToDtos(List<Publisher> publishers);

}
