package com.dictify

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class LanguageAdapter(context: Context, private val languages: List<Language>) :
    ArrayAdapter<Language>(context, android.R.layout.simple_spinner_item, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.setTextColor(context.resources.getColor(R.color.black))
        val currentItem = getItem(position)
        currentItem?.let {
            // Customize
            (view.findViewById<TextView>(android.R.id.text1)).text = it.name
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val currentItem = getItem(position)
        currentItem?.let {
            // Customize
            (view.findViewById<TextView>(android.R.id.text1)).text = "${it.name} (${it.code})"
        }
        return view
    }
}
