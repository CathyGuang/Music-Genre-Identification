package com.example.shoppinglist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.ScrollingActivity
import com.example.shoppinglist.data.AppDatabase
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.touch.ShoppingTouchHelperCallback
import kotlin.concurrent.thread

class ShoppingRecyclerAdapter : androidx.recyclerview.widget.ListAdapter<ShoppingItem, ShoppingRecyclerAdapter.ViewHolder>, ShoppingTouchHelperCallback {

    val context: Context

    constructor(context: Context) : super(ShoppingDiffCallback()) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shoppingRowBinding = ShoppingRowBinding.inflate(LayoutInflater.from(context),
            parent, false)
        return ViewHolder(shoppingRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentShopping = getItem(holder.adapterPosition)
        holder.bind(currentShopping)

        holder.shoppingRowBinding.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.shoppingRowBinding.btnEdit.setOnClickListener {
            // Edit...
            (context as ScrollingActivity).showEditDialog(currentShopping)
        }

        holder.shoppingRowBinding.cbDone.setOnClickListener{
            currentShopping.done = holder.shoppingRowBinding.cbDone.isChecked
            thread {
                AppDatabase.getInstance(context).ItemDao().updateShopping(currentShopping)
            }
        }
    }


    fun deleteTodo(index: Int) {
        thread {
            AppDatabase.getInstance(context).ItemDao().deleteShopping(getItem(index))
        }
    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(val shoppingRowBinding: ShoppingRowBinding) : RecyclerView.ViewHolder(shoppingRowBinding.root) {
        fun bind(shoppingItem: ShoppingItem) {
            shoppingRowBinding.tvDate.text = shoppingItem.createDate
            shoppingRowBinding.cbDone.text = shoppingItem.title
            shoppingRowBinding.cbDone.isChecked = shoppingItem.done
        }
    }
}

class ShoppingDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
    override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem._id == newItem._id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem == newItem
    }
}