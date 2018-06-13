package com.wdeo3601.jszfloatmessageview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import com.jiushizhuan.release.utils.kotlin.dp2px
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

/**
 * Created by wendong on 2018/6/12 0012.
 * Email:       wdeo3601@163.com
 * Description:
 */
class JszFloatMessageView : FrameLayout {

    //全部开始
    private val STATUS_START = 1
    //其中一个正在展示
    private val STATUS_SHOWING = 2
    //其中一个展示结束
    private val STATUS_END = 3
    //空闲，全部展示结束
    private val STATUS_IDLE = 4

    private var messageTextView: TextView by Delegates.notNull()

    //用来存放需要展示的消息集合
    private var mMessageList: MutableList<CharSequence> = mutableListOf()
    private var mMyShowMessageHandler: MyShowMessageHandler by Delegates.notNull()
    private var mStatus: Int = STATUS_IDLE

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
        initMessageHandler()
    }


    private fun initView() {
        //初始化view
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams = params
        setPadding(dp2px(26f), 0,
                dp2px(26f), 0)
        messageTextView = createTextView()
        addView(messageTextView)
    }

    /**
     * 创建一个textview用来展示小喇叭
     */
    private fun createTextView(): TextView {
        val textView = TextView(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.layoutParams = params
        textView.setPadding(dp2px(10f), dp2px(4f), dp2px(10f), dp2px(4f))

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = dp2px(12.5f).toFloat()
        gradientDrawable.setColor(Color.argb(104, 0, 0, 0))
        textView.background = gradientDrawable

        val drawable = context.getDrawable(R.drawable.ic_float_message_horn)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        textView.setCompoundDrawables(drawable, null, null, null)
        textView.compoundDrawablePadding = dp2px(5f)

        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setTextColor(Color.WHITE)
        textView.textSize = 12f

        textView.setSingleLine()
        textView.ellipsize = TextUtils.TruncateAt.MARQUEE
        textView.marqueeRepeatLimit = -1
        textView.setHorizontallyScrolling(true)
        textView.isFocusableInTouchMode = true
        textView.isFocusable = true

        textView.visibility = View.INVISIBLE

        return textView
    }

    /**
     * 初始化处理小喇叭展示与隐藏的handler
     */
    private fun initMessageHandler() {
        mMyShowMessageHandler = MyShowMessageHandler(this)
    }

    /**
     * 自定义handler静态内部类，弱引用它的外部类实例
     */
    class MyShowMessageHandler(jszFloatMessageView: JszFloatMessageView) : Handler() {

        private val weakReference = WeakReference<JszFloatMessageView>(jszFloatMessageView)

        override fun handleMessage(msg: Message?) {
            val jszFloatMessageView = weakReference.get()
            if (jszFloatMessageView != null && msg != null) {
                handleStatus(jszFloatMessageView, msg.what)
            }
        }

        /**
         * 拿到handler进行消息分派
         */
        private fun handleStatus(jszFloatMessageView: JszFloatMessageView, what: Int) {
            when (what) {
                jszFloatMessageView.STATUS_START -> {
                    showIfNeeded(jszFloatMessageView)
                }
                jszFloatMessageView.STATUS_SHOWING -> {
                    setMessageToTextView(jszFloatMessageView)
                }
                jszFloatMessageView.STATUS_END -> {
                    hideTextViewWithAnim(jszFloatMessageView)
                }
            }
        }

        /**
         * 从新开始进行消息展示
         */
        private fun showIfNeeded(jszFloatMessageView: JszFloatMessageView) {
            if (jszFloatMessageView.mStatus == jszFloatMessageView.STATUS_IDLE) {
                jszFloatMessageView.mMyShowMessageHandler.sendEmptyMessage(jszFloatMessageView.STATUS_SHOWING)
            }
        }

        /**
         * 对小喇叭队列的单个消息进行展示
         */
        private fun setMessageToTextView(jszFloatMessageView: JszFloatMessageView) {
            //把小喇叭状态改为正在展示
            jszFloatMessageView.mStatus = jszFloatMessageView.STATUS_SHOWING
            if (jszFloatMessageView.mMessageList.isEmpty()) {
                throw KotlinNullPointerException("message is empty")
            }
            jszFloatMessageView.messageTextView.text = jszFloatMessageView.mMessageList[0]
            jszFloatMessageView.messageTextView.requestFocus()
            showTextViewWithAnim(jszFloatMessageView)

        }

        /**
         * 清空小喇叭内容
         */
        private fun clearMessageInTextView(jszFloatMessageView: JszFloatMessageView) {
            //修改状态字段，标记本次小喇叭逻辑已经完成
            jszFloatMessageView.mStatus = jszFloatMessageView.STATUS_END
            jszFloatMessageView.messageTextView.text = ""
            if (!jszFloatMessageView.mMessageList.isEmpty()) {
                //小喇叭消息还没有展示完，通知handler接着去展示
                jszFloatMessageView.mMyShowMessageHandler.sendEmptyMessage(jszFloatMessageView.STATUS_SHOWING)
            } else {
                //集合里已经没有小喇叭消息了，把状态置为空闲，为下次小喇叭消息的到来做准备
                jszFloatMessageView.mStatus = jszFloatMessageView.STATUS_IDLE
            }
        }

        /**
         * 显示textview并伴随显示动画
         */
        private fun showTextViewWithAnim(jszFloatMessageView: JszFloatMessageView) {
            jszFloatMessageView.messageTextView.visibility = View.VISIBLE
            val showAnimator = ValueAnimator.ofFloat(0f, 1f)
                    .setDuration(500)
            showAnimator.interpolator = AccelerateDecelerateInterpolator()
            showAnimator.addUpdateListener {
                jszFloatMessageView.messageTextView.alpha = it.animatedValue as Float
            }
            showAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    //动画执行结束，把刚才展示的小喇叭消息删掉，并通知handler本次小喇叭执行结束，需要隐藏了
                    jszFloatMessageView.mMessageList.removeAt(0)
                    jszFloatMessageView.mMyShowMessageHandler.sendEmptyMessageDelayed(jszFloatMessageView.STATUS_END, 4000)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            showAnimator.start()
        }

        /**
         * 消息显示完毕需要隐藏textview
         */
        private fun hideTextViewWithAnim(jszFloatMessageView: JszFloatMessageView) {
            val hideAnimtor = ValueAnimator.ofFloat(1f, 0f)
                    .setDuration(500)
            hideAnimtor.interpolator = AccelerateDecelerateInterpolator()
            hideAnimtor.addUpdateListener {
                jszFloatMessageView.messageTextView.alpha = it.animatedValue as Float
            }
            hideAnimtor.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    jszFloatMessageView.messageTextView.visibility = View.INVISIBLE
                    //隐藏动画执行完毕，需要清空小喇叭里的内容了
                    clearMessageInTextView(jszFloatMessageView)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            hideAnimtor.start()
        }
    }


    /**
     * 逐个添加将要显示的消息
     */
    fun addFloatMessage(message: CharSequence) {
        mMessageList.add(message)
    }

    /**
     * 开始显示悬浮消息
     */
    fun showFloatMessages() {
        if (mMessageList.isEmpty()) {
            throw KotlinNullPointerException("please invoke addFloatMessage() first")
        }
        mMyShowMessageHandler.sendEmptyMessage(STATUS_START)
    }

}