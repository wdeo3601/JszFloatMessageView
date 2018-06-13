package com.wdeo3601.jszfloatmessageview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        val floatMessageView = findViewById<JszFloatMessageView>(R.id.jsz_float_message_view)

        val messageList = mutableListOf("你不是一个人在战斗，还有猪队友",
                "不管被伤得有多重，一局之后满血复活",
                "狭路相逢勇者胜",
                "只有装备好自己，才能在人生战场上活下来")
        val random = Random()
        button.setOnClickListener {
            val index = random.nextInt(messageList.size)
            val message = messageList[index]
            floatMessageView.addFloatMessage(message)
            floatMessageView.showFloatMessages()
        }
    }
}
