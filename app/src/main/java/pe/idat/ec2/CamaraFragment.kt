package pe.idat.ec2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pe.idat.ec2.databinding.FragmentCamaraBinding

class CamaraFragment : Fragment() {
    private lateinit var binding: FragmentCamaraBinding
    private lateinit var openCameraLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCamaraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tomarFoto.setOnClickListener {
            if (permissionValidated()) {
                takePhoto()
            }
        }

        openCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoBitmap = result.data?.extras?.get("data") as Bitmap
                binding.imgPhoto.setImageBitmap(photoBitmap)
            }
        }
    }

    private fun takePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        openCameraLauncher.launch(cameraIntent)
    }

    private fun permissionValidated(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        )
        val permissionList: MutableList<String> = mutableListOf()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.CAMERA)
        }
        if (permissionList.isNotEmpty()) {
            requestPermissions(permissionList.toTypedArray(), 1000)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                }
            }
        }
    }
}
