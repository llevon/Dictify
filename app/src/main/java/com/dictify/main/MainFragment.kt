package com.dictify.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dictify.R
import com.dictify.databinding.FragmentMainBinding
import com.dictify.databinding.FragmentNotesBinding

class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding?: throw Exception("Binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
}

private fun setupNavigation() {
    val navHostFragment =
        childFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
    val navController = navHostFragment.navController
    binding.mainBottomNavigationBar.setupWithNavController(navController)
}
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
