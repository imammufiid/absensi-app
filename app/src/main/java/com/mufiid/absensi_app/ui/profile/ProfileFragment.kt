package com.mufiid.absensi_app.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.data.source.local.entity.UserEntity
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
    private var userEntity: UserEntity? = null

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

    @SuppressLint("SetTextI18n")
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

        viewModel.userData.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                userEntity = user
                _bind.txtFullName.text = user.name
                val nik = user.nik
                val startXNik = nik?.length?.minus(4) // 8
                val xNik = startXNik?.let { startXNik ->
                    nik.length.minus(startXNik).let { countRepeat -> "x".repeat(countRepeat) }
                }
                _bind.txtNik.text = nik?.substring(0, nik.length - 4) + xNik

                context?.let { context ->
                    Glide.with(context)
                        .load(user.profileImage)
                        .centerCrop()
                        .into(_bind.profileUser)
                }
            }
        })
    }

    private fun init() {
        _bind.btnEditProfile.setOnClickListener(this)
        _bind.btnLogout.setOnClickListener(this)

        val userPref = context?.let { context -> UserPref.getUserData(context) }
        userPref?.token?.let { token ->
            viewModel.getUser(token, userPref.id)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_edit_profile -> {
                startActivity(Intent(context, EditProfileActivity::class.java).apply {
                    putExtra(EditProfileActivity.USER, userEntity)
                })
            }
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