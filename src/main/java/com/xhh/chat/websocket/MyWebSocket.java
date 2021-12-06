package com.xhh.chat.websocket;

import com.xhh.chat.model.UserInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

@ServerEndpoint("/websocket/{nickName}")
@Component
public class MyWebSocket {

  //用来存放每个客户端对应的MyWebSocket对象。
  private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
  private static Map<Session, UserInfo> connectMap = new HashMap<>();//用session作为key，保存用户信息
  //与某个客户端的连接会话，需要通过它来给客户端发送数据
  private Session session;

  /**
   * 连接建立成功调用的方法
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("nickName") String nickName) {
    this.session = session;
    UserInfo userInfo = new UserInfo(session.getId(), nickName);
    connectMap.put(session, userInfo);
    webSocketSet.add(this);     //加入set中
    System.out.println(nickName + " 上线了！当前在线人数为" + webSocketSet.size());
    //群发消息，告诉每一位
    broadcast(nickName + " 上线了！-->当前在线人数为：" + webSocketSet.size());
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose() {
    String nickName = connectMap.get(session).getNickName();
    connectMap.remove(session);
    webSocketSet.remove(this);  //从set中删除
    System.out.println(nickName + " 下线了！当前在线人数为" + webSocketSet.size());
    //群发消息，告诉每一位
    broadcast(nickName + " 下线，当前在线人数为：" + webSocketSet.size());
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    System.out.println("来自客户端的消息:" + message);
    //群发消息
    String nickName = connectMap.get(session).getNickName();
    broadcast(nickName + " 说：" + message);
  }

  /**
   * 发生错误时调用
   */
  @OnError
  public void onError(Session session, Throwable error) {
    System.out.println("发生错误");
    error.printStackTrace();
  }

  /**
   * 群发自定义消息
   */
  public void broadcast(String message) {
    for (MyWebSocket item : webSocketSet) {
      //this.session.getBasicRemote().sendText(message);
      item.session.getAsyncRemote().sendText(message);//异步发送消息.
    }
  }
}
