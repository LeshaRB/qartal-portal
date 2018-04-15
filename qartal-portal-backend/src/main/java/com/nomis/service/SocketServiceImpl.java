package com.nomis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomis.shared.model.ServerInfo;
import com.nomis.shared.model.ServerStatus;
import com.nomis.shared.response.ServerStatusResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * SocketServiceImpl.
 *
 * @author Aliaksei Labotski.
 * @since 4/14/18.
 */
@Service
@Slf4j
public class SocketServiceImpl implements SocketService {

  private List<WebSocketSession> webSocketSessionList = new ArrayList<>();

  @Autowired
  private ServerService serverService;

  @Autowired
  private ObjectMapper objectMapper;

  @Scheduled(fixedRate = 3000)
  public void updateServerStatus() throws IOException {
    ServerStatusResponse serverStatusResponse = generateServerStatusResponse(serverService.getServerInfo()
        .getServerInfoList());
    webSocketSessionList.forEach(webSocketSession -> {
      try {
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(serverStatusResponse)));
      } catch (IOException e) {
        log.error("updateServerStatus", e);
      }
    });
  }

  @Override
  public List<WebSocketSession> getWebSocketSessionList() {
    return webSocketSessionList;
  }

  private ServerStatusResponse generateServerStatusResponse(List<ServerInfo> serverInfoList) {
    ServerStatusResponse serverStatusResponse = new ServerStatusResponse();
    List<ServerInfo> serverInfoListGen = new ArrayList<>();

    serverInfoList.forEach(serverInfo -> {
      ServerStatus serverStatus = ServerStatus.getRandomStatus();
      ServerInfo serverInfoGen = new ServerInfo();
      serverInfoGen.setId(serverInfo.getId());
      serverInfoGen.setServerType(serverInfo.getServerType());
      serverInfoGen.setServerStatus(serverStatus);
      serverInfoListGen.add(serverInfoGen);
    });

    serverStatusResponse.setServerInfoList(serverInfoListGen);
    return serverStatusResponse;
  }
}
