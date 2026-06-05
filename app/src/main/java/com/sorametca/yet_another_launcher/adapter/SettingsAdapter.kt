package com.sorametca.yet_another_launcher.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sorametca.yet_another_launcher.R

class SettingsAdapter(
    context: Context,
    private val titles: List<String>,
    private val icons: List<Int>
) : ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, titles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (convertView as? TextView)
            ?: LayoutInflater.from(context)
                .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false) as TextView

        view.text = titles[position]

        val drawable: Drawable? = ContextCompat.getDrawable(context, icons[position])

        drawable?.let {
            val sizeInPx = (24 * context.resources.displayMetrics.density).toInt()
            it.setBounds(0, 0, sizeInPx, sizeInPx)
        }

        view.setCompoundDrawablesRelative(drawable, null, null, null)

        return view
    }
}