package com.example.yunusileri.qr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.contact_data.view.*

class ListAdapter(val context: Context, val list: ArrayList<Contact>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.contact_data, parent, false)

        view.contact_name.text = list[position].name
        view.contact_phone_number.text = list[position].phoneNumber

        return view
    }

    override fun getItem(position: Int): Contact {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}