package com.mufiid.absensi_app.ui.bsuploadfile

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.databinding.FragmentBottomSheetUploadFileTaskBinding
import com.mufiid.absensi_app.ui.home.HomeFragment
import com.mufiid.absensi_app.ui.profileedit.EditProfileActivity
import com.mufiid.absensi_app.ui.task.TaskFragment
import com.mufiid.absensi_app.ui.task.TaskViewModel
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class BottomSheetUploadFileTask : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var _bind : FragmentBottomSheetUploadFileTaskBinding
    private lateinit var viewModel: TaskViewModel
    private var buttonPickFile : Button? = null
    private var buttonSend : Button? = null
    private var buttonListener: ButtonListener? = null

    companion object {
        const val TAG = "bottom_sheet_upload_file"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentBottomSheetUploadFileTaskBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[TaskViewModel::class.java]

        _bind.btnUploadFile.setOnClickListener(this)
        _bind.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_upload_file -> {
                if (buttonListener != null) buttonListener?.pickFile()
            }
            R.id.btn_save -> {
                if (buttonListener != null) buttonListener?.send()
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment
        if (fragment is TaskFragment) {
            this.buttonListener = fragment.buttonListener
        } else if (fragment is HomeFragment) {
            this.buttonListener = fragment.buttonListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.buttonListener = null
    }

    interface ButtonListener {
        fun pickFile()
        fun send()
    }
}