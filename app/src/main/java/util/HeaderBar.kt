package util

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.wisdom.myapplication.R


import kotlinx.android.synthetic.main.layout_header_bar.view.*

/*
    Header Bar封装
 */
class HeaderBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 是否显示"返回"图标
     */
    private var isShowBack = true
    /**
     * Title文字
     */
    private var titleText: String? = null
    /**
     * 右侧图标
     */
    private var isRightShow = false
    /**
     * 右侧文字
     */
    private var rightText: String? = null

    init {
        //获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderBar)

        isShowBack = typedArray.getBoolean(R.styleable.HeaderBar_isShowBack, true)

        titleText = typedArray.getString(R.styleable.HeaderBar_titleText)
        isRightShow = typedArray.getBoolean(R.styleable.HeaderBar_isRightShow, false)
        rightText = typedArray.getString(R.styleable.HeaderBar_rightText)
        initView()
        typedArray.recycle()
    }

    /**
     *  初始化视图
     */
    private fun initView() {
        View.inflate(context, R.layout.layout_header_bar, this)

        mLeftIv.visibility = if (isShowBack) View.VISIBLE else View.GONE
        mRightIv.visibility = if (isRightShow) View.VISIBLE else View.GONE
        //标题不为空，设置值
        titleText?.let {
            mTitleTv.text = it
        }

        //右侧文字不为空，设置值
        rightText?.let {
            mRightText.text = it
            mRightText.visibility = View.VISIBLE
        }

        //返回图标默认实现（关闭Activity）
        mLeftIv.setOnClickListener {
            if (context is Activity) {
                (context as Activity).finish()
            }
        }
    }

    /**
     *   获取左侧视图
     */
    fun getLeftView(): ImageView {
        return mLeftIv
    }

    /**
     *  获取右侧视图
     */
    fun getRightView(): ImageView {
        return mRightIv
    }

    /**
     *  获取右侧文字
     */
    fun getRightText(): String {
        return mRightText.text.toString()
    }

}
