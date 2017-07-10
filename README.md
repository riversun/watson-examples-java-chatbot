# Overview
This is the example of Java Chat Bot GUI for Watson Conversation using [watson-conversation-service-for-java](https://github.com/riversun/watson-conversation-service-for-java)

It is licensed under [MIT](https://opensource.org/licenses/MIT).

## How to import into your Eclipse and Run.

### Import into Eclipse

1.Select File>Import>Git - Projects from Git  
2.Clone URI  
3.set clone URI to https://github.com/riversun/watson-examples-java-chatbot.git  
4.Select next along the flow  
5.Check "Import as general project" and select "finish"  


### After import

1.Right click on Project  
2.Configure>Convert to Maven project  
3.(Now you can handle this project as a maven project)  


### How to run
- Check username,password,workspaceId of taget workspace of Watson Conversation.

<img src="https://riversun.github.io/wcs/img/wcs_check_workspaceid.jpg">

- Clone and edit constants.Replace WATSON_CONVERSATION_USERNAME,WATSON_CONVERSATION_PASSWORD,WATSON_CONVERSATION_WORKSPACE_ID to yours.

```java
    private static final String WATSON_CONVERSATION_USERNAME = "YOUR_WATON_CONVERSATION_USER_NAME_HERE";
    private static final String WATSON_CONVERSATION_PASSWORD = "YOUR_WATON_CONVERSATION_PASSWORD_HERE";
    private static final String WATSON_CONVERSATION_WORKSPACE_ID = "YOUR_WATON_CONVERSATION_WORKSPACE_ID_HERE";
```
  
- Run **org.example.wcs.chatgui.WatsonChat**


<img src="https://riversun.github.io/wcs/img/wcs_java_chat.jpg">

### Example workspace
You can download example workspace from here.

https://riversun.github.io/wcs/org.riversun.WcsContextTest.zip
