package org.tourBot.history.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tourBot.history.dto.FileDto;

@Mapper
public interface FileMapper {

    void insertFile(FileDto file);

    FileDto findById(@Param("fileId") String fileId);

    void deleteById(@Param("fileId") String fileId);
}
