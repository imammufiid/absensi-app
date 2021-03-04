package com.mufiid.absensi_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var _bind: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.text.observe(viewLifecycleOwner, {
            _bind.txtGreeting.text = getString(R.string.title_greeting_name, it)
        })
    }
}