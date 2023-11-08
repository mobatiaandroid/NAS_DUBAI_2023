package com.mobatia.naisapp.activity.home.adapter

import android.app.Activity
import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobatia.naisapp.R

class HomeListAdapter(
    private val context: Activity,
    private val title: Array<String>,
    private val imgid: TypedArray
) : ArrayAdapter<String>(context, R.layout.adapter_home, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.adapter_home, null, true)
        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView
        titleText.text = title[position]
        imageView.setImageResource(imgid.getResourceId(position, 0))
        return rowView
    }
}
