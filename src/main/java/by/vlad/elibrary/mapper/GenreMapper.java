package by.vlad.elibrary.mapper;

import by.vlad.elibrary.model.dto.request.GenreRequestDto;
import by.vlad.elibrary.model.dto.response.GenreResponseDto;
import by.vlad.elibrary.model.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    GenreResponseDto fromEntityToDto(Genre genre);

    List<GenreResponseDto> fromEntitiesToDtos(List<Genre> genres);

    Genre fromDtoToEntity(GenreRequestDto dto);
}
