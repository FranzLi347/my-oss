package io.github.franzli347.foss.middleware.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.franzli347.foss.support.wsSupport.WsSessionProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import io.github.franzli347.foss.model.vo.WsResult;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @ClassName WebSocketHandler
 * @Author AlanC
 * @Date 2022/12/29 16:26
 **/
@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final WsSessionProvider wsSessionProvider;
    private final ObjectMapper objectMapper;
    public WebSocketHandler(WsSessionProvider wsSessionProvider, ObjectMapper objectMapper) {
        this.wsSessionProvider = wsSessionProvider;
        this.objectMapper = objectMapper;
    }
    /**
     * socket 建立成功事件
     *
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        String userId = Optional.ofNullable((String) session.getAttributes().get("id")).orElseThrow(()->new RuntimeException("用户验证失败!"));
            // 用户连接成功，放入redis
        wsSessionProvider.add(userId,session);
    }
    /**
     * 接收消息事件
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        Object userId = session.getAttributes().get("id");
        session.sendMessage(new TextMessage("server 发送给 " + userId + " 消息 " + payload + " " + LocalDateTime.now()));
    }

    /**
     * socket 断开连接时
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
            // 用户退出，移除缓存
        wsSessionProvider.removeAndClose(Optional.ofNullable(session.getAttributes().get("id")).toString());
    }
    @SneakyThrows
    public void sendResultMsg(WsResult result){
        WebSocketSession webSocketSession= wsSessionProvider.get(result.getUserId());
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
    }
}
