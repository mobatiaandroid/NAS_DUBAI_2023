package com.mobatia.naisapp.constant

import android.graphics.Point
import android.view.View

class DragShadowBuilder(view: View?) : View.DragShadowBuilder(view) {
    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
        outShadowSize!!.set(1, 1)
        outShadowTouchPoint!!.set(0, 0)
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
    }
}