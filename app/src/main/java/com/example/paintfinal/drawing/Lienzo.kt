package com.example.paintfinal.drawing

import android.content.Context
import android.database.Observable
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import com.example.paintfinal.R
import kotlin.math.abs

class Lienzo @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    //el background que coja el color que hemos creado inicialmente en colors.xml
    var bgColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    companion object {
        private const val STROKE_WIDTH = 12f
    }

    data class DrawInfo(var path: Path, var paint: Paint)

    var paint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
        //suavizar curvas
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }
    private var mode = 1
    private var toleranceUp = false
    private var fill = false
    private var path = Path()
    private var drawInfo = DrawInfo(path,paint)
    var drawing : MutableList<DrawInfo> = mutableListOf<DrawInfo>()
    var drawingRecycler : MutableList<DrawInfo> = mutableListOf<DrawInfo>()

    //coordenadas de donde toca el usuario
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    //tolerancia a movimientos
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //Para ajustarse al tamaño total de la pantalla del dispositivo
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(bgColor)
        //recorremos la lista de drawing y pintamos
        for (pathT in drawing){
            canvas.drawPath(pathT.path, pathT.paint)
        }
        if (fill && mode != 1) {
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)
        } else {
            paint.style = Paint.Style.STROKE
            canvas.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }

        return true
    }

    private fun touchUp() {
            //Hacemos un constructor para guardar el objeto y no la referencia, porque la referencia la estamos reseteando.
            drawInfo.path = Path(path)
            drawInfo.paint = Paint(paint)
            drawing.add(DrawInfo(drawInfo.path, drawInfo.paint))
            drawingRecycler.clear()
            path.reset()
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        //tras calcular las diferencias de x e y, se comparan estas con la tolerancia.
        //Si es mayor se pinta una líena con el progreso y se actualizan las posiciones.
        //invalidate hace que se vuelva a llamar a "onDraw()" para repintar.
        if (dx >= touchTolerance || dy >= touchTolerance) {
            when (mode) {
                1 -> {
                    path.quadTo(
                        currentX,
                        currentY,
                        (motionTouchEventX + currentX) / 2,
                        (motionTouchEventY + currentY) / 2
                    )
                    currentX = motionTouchEventX
                    currentY = motionTouchEventY

                }
                2 -> {
                    path.reset()

                    if(currentX <= motionTouchEventX && currentY <= motionTouchEventY) {

                        path.addArc(currentX, currentY, motionTouchEventX, motionTouchEventY, 0f, 360f)

                    } else if(currentY >= motionTouchEventY && currentX <= motionTouchEventX) {

                        path.addArc(currentX, motionTouchEventY, motionTouchEventX, currentY, 0f, 360f)

                    } else if(currentX >= motionTouchEventX && currentY <= motionTouchEventY) {

                        path.addArc(motionTouchEventX, currentY, currentX, motionTouchEventY, 0f, 360f)

                    } else if (currentX >= motionTouchEventX && currentY >= motionTouchEventY){

                        path.addArc(motionTouchEventX, motionTouchEventY, currentX, currentY, 0f, 360f)

                    }

                }
                else -> {
                    path.reset()
                    if(currentX <= motionTouchEventX && currentY <= motionTouchEventY) {

                        path.addRect(currentX, currentY, motionTouchEventX, motionTouchEventY, Path.Direction.CW)

                    } else if(currentY >= motionTouchEventY && currentX <= motionTouchEventX) {

                        path.addRect(currentX, motionTouchEventY, motionTouchEventX, currentY, Path.Direction.CW)

                    } else if(currentX >= motionTouchEventX && currentY <= motionTouchEventY) {

                        path.addRect(motionTouchEventX, currentY, currentX, motionTouchEventY, Path.Direction.CW)

                    } else if (currentX >= motionTouchEventX && currentY >= motionTouchEventY){

                        path.addRect(motionTouchEventX, motionTouchEventY, currentX, currentY, Path.Direction.CW)

                    }
                }
            }
            invalidate()

        }

    }

    private fun touchStart() {
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY

    }

    fun undo() {
        drawingRecycler.add(drawing.removeLast())
        invalidate()
    }

    fun redo() {
        drawing.add(drawingRecycler.removeLast())
        invalidate()
    }

    fun clear() {
        drawing.clear()
        drawingRecycler.clear()
        invalidate()
    }

    fun setStroke(strokeWidth: Float) {
       paint.strokeWidth = strokeWidth
    }

    fun setColor(paintColor: Int) {
        paint.color = paintColor
    }

    fun setDefaultColor() {
        paint = Paint().apply {
            color = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
            //suavizar curvas
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE // default: FILL
            strokeJoin = Paint.Join.ROUND // default: MITER
            strokeCap = Paint.Cap.ROUND // default: BUTT
            strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
        }
    }

    fun setMode(mode: Int) {
        this.mode = mode
    }

    fun setFill(fill: Boolean) {
        this.fill = fill
    }








}