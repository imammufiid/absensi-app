package com.skripsi.absensi_app.ui.ijinattendance

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.databinding.FragmentBottomSheetIjinAttendanceBinding
import com.skripsi.absensi_app.databinding.FragmentBottomSheetSickAttendanceBinding
import com.skripsi.absensi_app.ui.home.HomeFragment

class BottomSheetIjinAttendance : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var _bind : FragmentBottomSheetIjinAttendanceBinding
    private var buttonListener: ButtonListener? = null

    companion object {
        const val TAG = "bottom_sheet_ijin_menu"
        const val FILENAME = "filename"
        const val ATTENDANCE_TYPE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentBottomSheetIjinAttendanceBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind.btnSave.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var fileName: String?
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
            R.id.btn_save -> {
                val information = _bind.descInformation.text.toString()
                if (buttonListener != null) buttonListener?.send(information)
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment
        if (fragment is HomeFragment) {
            this.buttonListener = fragment.buttonListenerIjinAttendance
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.buttonListener = null
    }

    interface ButtonListener {
        fun send(information: String?)
    }
}