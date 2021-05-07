package com.skripsi.absensi_app.ui.sickAttendance

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.databinding.FragmentBottomSheetSickAttendanceBinding
import com.skripsi.absensi_app.ui.home.HomeFragment

class BottomSheetSickAttendance : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var _bind : FragmentBottomSheetSickAttendanceBinding
    private var buttonListener: ButtonListener? = null

    companion object {
        const val TAG = "bottom_sheet_upload_file"
        const val FILENAME = "filename"
        const val PICK_FILE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentBottomSheetSickAttendanceBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind.btnUploadFile.setOnClickListener(this)
        _bind.btnSave.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var fileName: String? = ""
        // get param
        if (savedInstanceState != null) {
            fileName = savedInstanceState.getString(FILENAME)
        }

        if (arguments != null) {
            fileName = arguments?.getString(FILENAME)

            _bind.fileName.visibility = View.VISIBLE
            _bind.fileName.text = fileName
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_upload_file -> {
                if (buttonListener != null) buttonListener?.pickFile()
                dialog?.dismiss()
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
        if (fragment is HomeFragment) {
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