package com.skripsi.absensi_app.ui.profile

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.databinding.ProfileFragmentBinding
import com.skripsi.absensi_app.ui.login.LoginActivity
import com.skripsi.absensi_app.ui.profileedit.EditProfileActivity
import com.skripsi.absensi_app.utils.pref.BasePref
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var _bind: ProfileFragmentBinding
    private lateinit var viewModel: ProfileViewModel
    private var userEntity: UserEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

                // cara 1
                // lambda expression
//                setNik(user.nik) { newNik ->
//                    // newNik is return setNik function
//                    _bind.txtNik.text = newNik
//                }

                // cara 2
                _bind.txtNik.text = nik(user.nik, encryptNik)

                context?.let { context ->
                    Glide.with(context)
                        .load(user.profileImage)
                        .centerCrop()
                        .into(_bind.profileUser)
                }
            }
        })
    }

    // cara 1
    // high order function
    private fun setNik(nik: String?, result: (String) -> Unit) {
        val startNik = nik?.length?.minus(4)
        val xNik = startNik?.let {
            nik.length.minus(startNik).let { countRepeat -> "x".repeat(countRepeat) }
        }
        val concat = nik?.substring(0, nik.length - 4) + xNik

        // return result
        result(concat)
    }

    // cara 2
    // lambda operation
    // nik is input
    // concat is output
    private val encryptNik = { nik: String? ->
        val startNik = nik?.length?.minus(4)
        val xNik = startNik?.let {
            nik.length.minus(startNik).let { countRepeat -> "x".repeat(countRepeat) }
        }
        val concat = nik?.substring(0, nik.length - 4) + xNik
        concat
    }

    // high order function
    private fun nik(nik: String?, result: (String) -> String): String {
        val newNik = nik?.let { result(it) }
        return "$newNik"
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
                    val userPref = context?.let { context -> UserPref.getUserData(context) }
                    UserPref.getUserData(context)?.token?.let { token ->
                        viewModel.logout(
                            token, userPref?.id
                        )
                    }
                }
            }
        }
    }

}