package com.dictify.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dictify.databinding.FragmentHomeFirstBinding
import com.dictify.databinding.FragmentNotesBinding

class FirstHomeFragment : Fragment() {
    private var _binding: FragmentHomeFirstBinding? = null
    private val binding: FragmentHomeFirstBinding
        get() = _binding?: throw Exception("Binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeFirstBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}