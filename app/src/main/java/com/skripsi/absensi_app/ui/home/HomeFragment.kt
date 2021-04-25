package com.skripsi.absensi_app.ui.home

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity
import com.skripsi.absensi_app.databinding.FragmentHomeBinding
import com.skripsi.absensi_app.ui.bsuploadfile.BottomSheetUploadFileTask
import com.skripsi.absensi_app.ui.task.TaskAdapter
import com.skripsi.absensi_app.ui.task.TaskFragment
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var _bind: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskAdapter: TaskAdapter
    private var filePath: String? = null
    private var taskEntity: TaskEntity? = null
    private var part: MultipartBody.Part? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        homeViewModel = ViewModelProvider(this, factory)
            .get(HomeViewModel::class.java)

        init()
        // observe view model
        observerViewModel()
        getDateTimeToday()
    }

    private fun init() {
//        taskAdapter = TaskAdapter { item ->
//            taskEntity = item
//
//            BottomSheetUploadFileTask().show(
//                childFragmentManager,
//                BottomSheetUploadFileTask.TAG
//            )
//        }
        taskAdapter = TaskAdapter(object : TaskAdapter.CheckListTask {
            override fun showUploadFile(item: TaskEntity?) {
                taskEntity = item

                BottomSheetUploadFileTask().show(
                    childFragmentManager,
                    BottomSheetUploadFileTask.TAG
                )
            }

            override fun downloadFile(filePath: String?) {
                val path = URI(filePath).path
                val nameFile = path.substring(path.lastIndexOf("/") + 1)
                val request = DownloadManager.Request(Uri.parse(filePath)).apply {
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    setTitle("Donwload file tugas")
                    setDescription("Deskripsi file tugas")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}_${nameFile}"
                    )
                }

                val manager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)
            }
        })
        // taskAdapter.notifyItemChanged(1)

        setRecyclerView()
        setViewModel()

        // set click listener
        _bind.btnScan.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_homeFragment_to_scannerFragment,
                null
            )
        )
    }

    private fun setViewModel() {
        // set greeting name
        val name = context?.let { UserPref.getUserData(it)?.name }
        homeViewModel.setGreeting(name)

        // get all task
        val userPref = context?.let { UserPref.getUserData(it) }
        userPref?.token?.let { token ->
            homeViewModel.getTaskData(
                token, userPref.id, null, null
            )

            homeViewModel.attendanceToday(
                token, userPref.id
            )

            homeViewModel.getMyPoint(
                token, userPref.id
            )
        }
    }

    private fun setRecyclerView() {
        with(_bind.rvTask) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    internal var buttonListener: BottomSheetUploadFileTask.ButtonListener =
        object : BottomSheetUploadFileTask.ButtonListener {
            override fun pickFile() {
                openFile()
            }

            override fun send() {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Bearer ${context?.let { context -> UserPref.getUserData(context)?.token }}"
                val taskId =
                    taskEntity?.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val userId =
                    context?.let { context ->
                        UserPref.getUserData(context)?.id.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    }

                val file = File(filePath)
                val reqFile = file.asRequestBody("*/*".toMediaTypeOrNull())
                part = MultipartBody.Part.createFormData("file", file.name, reqFile)

                homeViewModel.markCompleteTask(
                    headers, taskId, userId, part
                )
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == TaskFragment.FILE_PICK_CODE) {
            val uri = data?.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                uri?.let { uri -> context?.contentResolver?.query(uri, filePath, null, null, null) }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePath[0])
            val picturePath = columnIndex?.let { columnIndex -> cursor.getString(columnIndex) }
            cursor?.close()
            this.filePath = picturePath.toString()
            val splitFileName = this.filePath?.split("/")
            val bundle = Bundle().apply {
                putString(BottomSheetUploadFileTask.FILENAME, splitFileName?.last())
            }
            BottomSheetUploadFileTask().apply {
                arguments = bundle
            }.show(
                childFragmentManager,
                BottomSheetUploadFileTask.TAG
            )
        }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        startActivityForResult(intent, TaskFragment.FILE_PICK_CODE)
    }

    private fun openFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context?.let { context ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                activity?.let { activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
            } else {
                pickFile()
            }
        } else {
            pickFile()
        }
    }

    private fun getDateTimeToday() {
        var date: String = ""
        date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            current.format(formatter)
        } else {
            Date().toString()
        }

        _bind.dateToday.text = date
    }

    private fun observerViewModel() {

        homeViewModel.greeting.observe(viewLifecycleOwner, {
            _bind.txtGreeting.text = getString(R.string.title_greeting_name, it)
        })

        homeViewModel.loading.observe(viewLifecycleOwner, {
            Log.d("LD_LOAD", it.toString())
            if (it) {
                _bind.progressBar.visibility = View.VISIBLE
            } else {
                _bind.progressBar.visibility = View.GONE
            }
        })

        homeViewModel.msgGetTaskData.observe(viewLifecycleOwner, {
            Log.d("LD_MSG_TASK", it.toString())
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        homeViewModel.msgAttendanceToday.observe(viewLifecycleOwner, {
            Log.d("LD_MSG_ATD", it.toString())
            if (it != null) {
                Snackbar.make(_bind.root, it, Snackbar.LENGTH_SHORT).show()
            }
        })

        homeViewModel.msgPoint.observe(viewLifecycleOwner, {
            Log.d("LD_MSG_POINT", it.toString())
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        homeViewModel.taskData.observe(viewLifecycleOwner, {
            Log.d("LD_TASK", it.toString())
            if (!it.isNullOrEmpty()) {
                taskAdapter.apply {
                    addTask(it)
                }
            } else {
                _bind.tvEmpty.visibility = View.VISIBLE
                _bind.tvEmpty.text = getString(R.string.data_empty, "tugas hari ini")
            }
        })

        homeViewModel.attendanceToday.observe(viewLifecycleOwner, {
            Log.d("LD_ATD", it.toString())
            if (it != null) {
                _bind.timeIn.text =
                    if (it.timeComes == "0") getString(R.string.time) else it.timeComes
                _bind.timeOut.text =
                    if (it.timeGohome == "0") getString(R.string.time) else it.timeGohome
            }
        })

        homeViewModel.pointData.observe(viewLifecycleOwner, {
            Log.d("LD_POINT", it.toString())
            if (it != null) {
                _bind.score.text = it
            }
        })
    }
}