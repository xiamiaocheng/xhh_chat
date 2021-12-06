package com.xhh.chat.model;

import lombok.Data;

@Data
public class UserInfo {

  private String id;
  private String nickName;
  private String password;

  public UserInfo(String id, String nickName) {
    this.id = id;
    this.nickName = nickName;
  }
}
