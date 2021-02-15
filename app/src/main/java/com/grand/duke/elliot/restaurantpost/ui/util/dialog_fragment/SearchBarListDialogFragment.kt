package com.grand.duke.elliot.restaurantpost.ui.util.dialog_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.grand.duke.elliot.restaurantpost.R
import com.grand.duke.elliot.restaurantpost.databinding.FragmentSearchBarListDialogBinding
import com.grand.duke.elliot.restaurantpost.ui.util.blank

abstract class SearchBarListDialogFragment<T>: DialogFragment() {

    private lateinit var binding: FragmentSearchBarListDialogBinding

    private var title = blank

    fun setTitle(title: String) {
        this.title = title
    }

    protected abstract val listAdapter: SearchBarListAdapter<T>?

    private var onClickListener: OnClickListener? = null
    private var onItemClickListener: SearchBarListAdapter.OnItemClickListener<T>? = null

    interface OnClickListener {
        fun onAddClick()
    }

    interface FragmentContainer {
        fun onRequestOnClickListener(): OnClickListener?
        fun <T> onRequestOnItemClickListener(): SearchBarListAdapter.OnItemClickListener<T>?
    }

    override fun onAttach(context: Context) {
        if (context is FragmentContainer) {
            onClickListener = context.onRequestOnClickListener()
            onItemClickListener = context.onRequestOnItemClickListener()
            onItemClickListener?.also { listAdapter?.setOnItemClickListener(it) }
        }

        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.WindowAnimations
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBarListDialogBinding.inflate(inflater, container, false)
        binding.textViewTitle.text = title
        binding.imageViewAdd.setOnClickListener {
            onClickListener?.onAddClick()
        }

        init()

        return binding.root
    }

    private fun init() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter?.getFilter()?.filter(newText)
                return true
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, boolean ->
            if (boolean) {
                if (binding.textViewTitle.isVisible)
                    binding.textViewTitle.visibility = View.GONE
            } else {
                binding.textViewTitle.visibility = View.VISIBLE
                binding.searchView.isIconified = true
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            listAdapter?.let { adapter = listAdapter }
        }
    }
}