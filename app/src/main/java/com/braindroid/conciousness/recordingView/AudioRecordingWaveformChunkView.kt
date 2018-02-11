package com.braindroid.conciousness.recordingView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import com.braindroid.conciousness.recordingView.AudioRecordingWaveformChunkView.PathMeasure.Specified
import com.braindroid.nervecenter.utils.Device
import kotlin.math.max
import kotlin.math.min

const val MIN_PATH_WIDTH = 2
const val MIN_PATH_HEIGHT = 4

class AudioRecordingWaveformChunkView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        this.currentWaveformChunk = WaveformChunk()
        this.currentOptions = Options()
    }

    data class Options(
        val padEqually: Boolean = true,
        val pathLateralPad: Int = 2,
        val pathVerticalPad: Int = 8,
        val pathWidth: PathMeasure = PathMeasure.Specified(MIN_PATH_WIDTH),
        val pathHeight: PathMeasure = PathMeasure.Specified(MIN_PATH_HEIGHT),
        val drawVertical: Boolean = false,
        var specComputedPathWidth: Int? = null,
        var specComputedPathHeight: Int? = null,
        var chunkPaint: Paint = Paint().apply {
            strokeCap = Paint.Cap.ROUND
            strokeWidth = MIN_PATH_WIDTH.toFloat()
            style = Paint.Style.FILL_AND_STROKE
            color = Color.BLACK
        }
    )

    sealed class PathMeasure {
        class Specified(val size: Int): PathMeasure()
        class Unspecified: PathMeasure()
    }

    private fun Options.totalLateralPad() = (if (padEqually) { pathLateralPad * 2 } else pathLateralPad / 2 )
    private fun Options.singleLateralPad() = (if (padEqually) { pathLateralPad } else pathLateralPad / 4 )
    private fun Options.totalVerticalPad() = (if (padEqually) { pathVerticalPad * 2 } else pathVerticalPad / 2 )
    private fun Options.singleVerticalPad() = (if (padEqually) { pathVerticalPad } else pathVerticalPad / 4 )

    // Get the width of a single drawn path, taking its padding into account
    private fun Options.pathWidthFromSpec(minPathWidth: Int = MIN_PATH_WIDTH): Int {
        val cacheFromSpec = max(minPathWidth,
            ((pathWidth as? Specified)?.size ?: minPathWidth) + totalLateralPad()
        )
        specComputedPathWidth = cacheFromSpec
        return cacheFromSpec
    }

    // Get the height of a single drawn path, taking its padding into account
    private fun Options.pathHeightFromSpec(minPathHeight: Int = MIN_PATH_HEIGHT): Int {
        val cacheFromSpec = max(minPathHeight,
            ((pathHeight as? Specified)?.size ?: minPathHeight) + totalVerticalPad()
        )
        specComputedPathHeight = cacheFromSpec
        return cacheFromSpec
    }

    var currentWaveformChunk: WaveformChunk = WaveformChunk()
        set(value) {
            field = value
            requestLayout()
        }

    var currentOptions: Options = Options()
        set(value) {
            if(field == null) {
                field = value
                return
            }
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val numChunks = currentWaveformChunk.displayPaths.size
        val minHeight = suggestedMinimumHeight
        val minWidth = suggestedMinimumWidth

        // TODO: Adhere to wrap_content / match_parent for minimums
        val singlePathWidth = currentOptions.pathWidthFromSpec()
        val singlePathHeight = currentOptions.pathHeightFromSpec(
            minHeight - currentOptions.totalVerticalPad()
        )

        val width = max(minWidth, when(widthMode) {
            EXACTLY -> { widthSize }
            AT_MOST -> { min(widthSize, singlePathWidth * numChunks) }
            UNSPECIFIED -> { singlePathWidth * numChunks }
            else -> { singlePathWidth * numChunks }
        })

        val height = max(minHeight, when(heightMode) {
            EXACTLY -> { heightSize }
            AT_MOST -> { min(heightSize, singlePathHeight) }
            UNSPECIFIED -> { singlePathHeight }
            else -> { singlePathHeight }
        })

        setMeasuredDimension(width, height)
//        startAnimating()
    }

    override fun willNotDraw(): Boolean = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas == null) return

        canvas.drawColor(Color.WHITE)

        if(currentWaveformChunk.displayPaths.isEmpty()) return
        if(currentOptions.specComputedPathHeight == null
            || currentOptions.specComputedPathWidth == null) return


//        val width = canvas.width
//        val height = canvas.height
//
//        val totalPathWidth = Device.dipToPx(currentOptions.specComputedPathWidth ?: 2, context)
//        val totalPathHeight = Device.dipToPx(currentOptions.specComputedPathHeight ?: height - 2, context)
//        val pathCount = currentWaveformChunk.displayPaths.size

        // TODO: support vertical drawing
        var xPos = 0f
        val singleLatPad = Device.dipToPx(currentOptions.singleLateralPad(), context)
        val singleVertPad = Device.dipToPx(currentOptions.singleVerticalPad(), context)

        currentWaveformChunk.displayPaths.forEach {
            xPos += singleLatPad
            canvas.drawLine(
                xPos, it[1] - singleVertPad,
                xPos, singleVertPad + it[0],
                currentOptions.chunkPaint
            )
            xPos += currentOptions.chunkPaint.strokeWidth
            xPos += singleLatPad
        }
    }

    var scale = 0.5f
    var scaleMa = 1f
    var scaleMi = 0.25f
    var inc = true
    private fun startAnimating() {
        postDelayed({
            if(inc) scale += 0.05f else scale -= 0.05f
            if(scale >= scaleMa) inc = false
            if(scale <= scaleMi) inc = true
//            requestLayout()
            invalidate()
            startAnimating()
        }, 500)
    }
}

data class WaveformChunk(
    val displayPaths: List<FloatArray> = emptyList()
)