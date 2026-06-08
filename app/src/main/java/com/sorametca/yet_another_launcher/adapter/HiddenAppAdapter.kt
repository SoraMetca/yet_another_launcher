package com.sorametca.yet_another_launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatButton
import android.widget.ImageView
import android.widget.TextView
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.model.AppObject

class HiddenAppAdapter(
    private val context: Context,
    private val apps: ArrayList<AppObject>,
    private val onUnhide: (AppObject) -> Unit
) : BaseAdapter() {

    override fun getCount() = apps.size

    override fun getItem(position: Int) = apps[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.hidden_app, parent, false)

        val app = apps[position]

        val icon = view.findViewById<ImageView>(R.id.icon)
        val label = view.findViewById<TextView>(R.id.label)
        val unhide = view.findViewById<AppCompatButton>(R.id.unhide_button)

        icon.setImageDrawable(app.icon)
        label.text = app.label

        unhide.setOnClickListener {
            onUnhide(app)

            apps.remove(app)
            notifyDataSetChanged()
        }

        return view
    }
}