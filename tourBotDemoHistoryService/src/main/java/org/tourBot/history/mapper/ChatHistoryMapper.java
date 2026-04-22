package org.tourBot.history.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tourBot.history.domain.ChatHistory;

import java.util.List;

@Mapper
public interface ChatHistoryMapper {

    List<ChatHistory> getHistory(@Param("userId") String userId, @Param("sessionId") String sessionId);

    void saveHistory(ChatHistory chatHistory);
}
