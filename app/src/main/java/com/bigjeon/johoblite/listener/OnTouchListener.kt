package com.bigjeon.johoblite.listener

import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bigjeon.johoblite.analysis.CadFragment.Companion.downX
import com.bigjeon.johoblite.analysis.CadFragment.Companion.downY

interface OnTouchListener: View.OnTouchListener {

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event!!.action) {
            MotionEvent.ACTION_DOWN -> {

                downX = event.x
                downY = event.y
                // downXY = downX + downY

                return false
            }
            MotionEvent.ACTION_UP -> {
                val upX:Float = event.x
                val upY:Float = event.y

                val upX_Str = upX.toString()
                val upY_Str = upY.toString()
                var upXY_Str = "$upX_Str, $upY_Str"

                val upX_min:Float = upX - 50
                val upY_min:Float = upY - 50
                val upX_max:Float = upX + 50
                val upY_max:Float = upY + 50

                if (downX > upX_min && downX < upX_max && downY > upY_min && downY < upY_max) {
                    Log.d("webView:OnTouchListener", "click -> $upXY_Str")
                    //Toast.makeText(v?.context, "좌표 : $upXY_Str", Toast.LENGTH_SHORT).show()
                    return true
                }

                return false
            }
        }
        return false
    }
    /*
    override fun onCapturedPointer(view: View?, event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        val xy = "$x, $y"

        return true
    }
    */
}