package com.mufiid.absensi_app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.ProfileFragmentBinding
import com.mufiid.absensi_app.ui.login.LoginActivity
import com.mufiid.absensi_app.ui.profileedit.EditProfileActivity
import com.mufiid.absensi_app.utils.pref.BasePref
import com.mufiid.absensi_app.utils.pref.UserPref
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

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
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observerViewModel()
    }

    private fun observerViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                _bind.progressBar.visibility = View.VISIBLE
                _bind.btnLogout.visibility = View.GONE
            } else {
                _bind.progressBar.visibility = View.GONE
                _bind.btnLogout.visibility = View.VISIBLE
            }
        })

        viewModel.message.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.response.observe(viewLifecycleOwner, {
            if (it?.isSuccess == true) {
                context?.let { context -> BasePref.clearPrefAuth(context) }
                startActivity(Intent(context, LoginActivity::class.java))
                activity?.finish()
            }
        })
    }

    private fun init() {
        _bind.btnEditProfile.setOnClickListener(this)
        _bind.btnLogout.setOnClickListener(this)

        // get Pref User
        getPrefUser()
    }

    private fun getPrefUser() {
        context?.let {
            UserPref.getUserData(it).let { user ->
                _bind.txtFullName.text = user?.name
                _bind.txtNik.text = user?.nik
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_edit_profile -> startActivity(Intent(context, EditProfileActivity::class.java))
            R.id.btn_logout -> {
                context?.let { context ->
                    UserPref.getUserData(context)?.token?.let { token ->
                        viewModel.logout(
                            token
                        )
                    }
                }
            }
        }
    }

}