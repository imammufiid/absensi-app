package com.mufiid.absensi_app.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.ProfileFragmentBinding
import com.mufiid.absensi_app.ui.profileedit.EditProfileActivity

class ProfileFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var _bind: ProfileFragmentBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = ProfileFragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        _bind.btnEditProfile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_edit_profile -> startActivity(Intent(context, EditProfileActivity::class.java))
        }
    }

}