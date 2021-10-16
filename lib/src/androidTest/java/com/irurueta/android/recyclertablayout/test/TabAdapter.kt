package com.irurueta.android.recyclertablayout.test

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TabAdapter(val count: Int) : RecyclerView.Adapter<TabViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val v = TabView(parent.context)
        return TabViewHolder(v)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.view.tabName = position.toString()
    }

    override fun getItemCount(): Int {
        return count
    }
}