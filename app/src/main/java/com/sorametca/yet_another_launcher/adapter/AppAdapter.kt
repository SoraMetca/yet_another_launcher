package com.sorametca.yet_another_launcher.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sorametca.yet_another_launcher.AppObject
import com.sorametca.yet_another_launcher.R
import com.sorametca.yet_another_launcher.fragments.AppDrawerContextMenu
import com.sorametca.yet_another_launcher.fragments.SortAppsFragment

class AppAdapter(
    private val context: Context,
    private val originalList: ArrayList<AppObject>
) : BaseAdapter(), Filterable {

    private var filteredList: ArrayList<AppObject> = ArrayList(originalList)

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): Any = filteredList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var v: View
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.item_app, parent, false)
        } else {
            v = convertView
        }
        val icon: ImageView = v.findViewById(R.id.icon)
        val label: TextView = v.findViewById(R.id.label)

        icon.setImageDrawable(filteredList[position].icon)
        label.text = filteredList[position].label

        val mLinearLayout = v.findViewById<LinearLayout>(R.id.Layout)
        mLinearLayout.setOnClickListener {
            val launchAppIntent =
                context.packageManager.getLaunchIntentForPackage(filteredList[position].packageName)
            if (launchAppIntent != null) {
                context.startActivity(launchAppIntent)
            }
        }

        mLinearLayout.setOnLongClickListener {
            val fragment = AppDrawerContextMenu()

            val location = IntArray(2)
            mLinearLayout.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1]

            val bundle = Bundle()
            bundle.putInt("x", x)
            bundle.putInt("y", y)
            fragment.arguments = bundle

            val activity = context as? FragmentActivity
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.main, fragment)
                ?.addToBackStack(null)
                ?.commit()
            true
        }
        return v
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = FilterResults()

                results.values = if (query.isEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.label.lowercase().contains(query)
                    } as ArrayList<AppObject>
                }

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<AppObject>
                notifyDataSetChanged()
            }
        }
    }
}