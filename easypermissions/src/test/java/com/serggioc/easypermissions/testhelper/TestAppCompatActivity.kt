package com.serggioc.easypermissions.testhelper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.serggioc.easypermissions.AfterPermissionGranted
import com.serggioc.easypermissions.EasyPermissions
import com.serggioc.easypermissions.R

class TestAppCompatActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        theme.applyStyle(R.style.Theme_AppCompat, true)
        super.onCreate(savedInstanceState)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

    }

    @AfterPermissionGranted(REQUEST_CODE)
    fun afterPermissionGranted() {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    companion object {
        const val REQUEST_CODE = 3
    }

}
