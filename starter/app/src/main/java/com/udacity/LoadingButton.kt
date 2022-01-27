package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    var buttonText = resources.getString(R.string.button_name)
    private var rect = RectF()
    private var topRect = RectF()

    private var customColor = resources.getColor(R.color.colorPrimary)
    private var customTextColor = resources.getColor(R.color.white)
    private var customCircleColor = resources.getColor(R.color.colorAccent)
    private var customProgressColor: Int = resources.getColor(R.color.colorPrimaryDark)
    private var progress: Float = 0f
    private var valueAnimator = ValueAnimator()
    private val ovalRect = RectF()

    private val paint_Background = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        color = customColor
        textAlign = Paint.Align.CENTER
    }
    private val paint_Text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = customTextColor
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        textSize = 60f
    }
    private val paint_Circle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = customCircleColor
    }
    private val paint_PreogressBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = customProgressColor
        style = Paint.Style.FILL
    }


    init {
        isClickable = true
        //init the ValueAnimator
        valueAnimator = ValueAnimator.ofFloat(0f, 720f)
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE

        //setting the Attributes
        //getting which color to draw the background with (customColor)
        //getting which color to draw the progress Animation with (customProgressColor)
        //getting which color to draw the Circle Animation with (customCircleColor)
        //getting which color to draw the Text with (customTextColor)
        attrs?.let { attributeSet ->
            val attributs = context.obtainStyledAttributes(attributeSet, R.styleable.LoadingButton)
            try {
                customColor = attributs.getColor(R.styleable.LoadingButton_customcolor, Color.GRAY)
                customProgressColor =
                    attributs.getColor(R.styleable.LoadingButton_custimProgresColor, Color.DKGRAY)
                customCircleColor =
                    attributs.getColor(R.styleable.LoadingButton_customCircleColor, Color.YELLOW)
                customTextColor =
                    attributs.getColor(R.styleable.LoadingButton_customTextColor, Color.WHITE)
            } finally {
                attributs.recycle()
            }
        }
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        //deciding when to start the valueAnimator according to the ButtonState
        //setting the Progress value by the animated value
        when (new) {
            ButtonState.Loading -> {
                isClickable = false
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator.start()
                valueAnimator.addUpdateListener { animation ->
                    progress = animation.animatedValue as Float
                    invalidate()
                }
            }
            ButtonState.Completed -> {
                isClickable = true
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.end()
//                invalidate()
            }
            ButtonState.Clicked -> {
                isClickable = true
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.end()
//                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.left = 0f
        rect.right = widthSize.toFloat()
        rect.top = 0f
        rect.bottom = heightSize.toFloat()

        topRect = rect

        paint_Background.color = customColor
        paint_Text.color = customTextColor

        canvas.drawRect(rect, paint_Background)


        //when the button state is Loading draw the Animation canvas
        when (buttonState) {
            ButtonState.Loading -> {
                if (progress == 720f) {
                    topRect.left = 0f
                } else {
                    topRect.right = widthSize * (progress / 720)
                }
                canvas.drawRect(topRect, paint_PreogressBar)

                ovalRect.top = 0f + paint_Text.descent()
                ovalRect.left = ((widthSize / 4) * 3 - 30).toFloat()
                ovalRect.right = ((widthSize / 4) * 3 + 30).toFloat()
                ovalRect.bottom = (heightSize).toFloat() - paint_Text.descent()
                canvas.drawArc(ovalRect, 0f, topRect.right / 2.5f, true, paint_Circle)

                canvas.drawText(
                    buttonText,
                    (widthSize / 2).toFloat(),
                    (heightSize / 2).toFloat(),
                    paint_Text
                )

            }
            else -> {
                canvas.drawText(
                    buttonText,
                    (widthSize / 2).toFloat(),
                    (heightSize / 2).toFloat(),
                    paint_Text
                )
            }
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun changState(state: ButtonState) {
        buttonState = state
    }
}