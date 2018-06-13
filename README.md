# JszFloatMessageView
一个全局小喇叭自定义view  
### 效果图  
![效果图](https://github.com/wdeo3601/JszFloatMessageView/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE.gif)
### 原理  
代码里注释已经写的很详细了。大概思路是通过使用一个集合，每次来消息时，都加入到集合最后一个元素，然后每次从集合中拿出第一个元素去展示，展示完成后从集合中删除掉。。  
### 使用场景  
做这个自定义view是为了展示从 websocket 主动发过来的小喇叭消息，如果前边有在显示的小喇叭消息，就需要等前边的消息依次显示完毕后再显示刚接收到的。  
### 使用方法  
1. 在布局文件中写  
```javascript
    <com.wdeo3601.jszfloatmessageview.JszFloatMessageView
        android:id="@+id/jsz_float_message_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```
2. 当需要设置消息的时候：
```javascript
  floatMessageView.addFloatMessage(message)
  floatMessageView.showFloatMessages()
```
### Todo（如果有时间的话）  
1. 自定义属性（字体大小、颜色，背景色，圆角大小，展示时间、现实和隐藏的时间） 
1. 添加消息的时候可以同时添加多条  
1. 点击事件回调  
### 联系方式  
QQ：974826191  
CSDN：https://blog.csdn.net/captive_rainbow_  
email：wdeo3601@163.com  
迫切希望共同探讨共同进步~~
