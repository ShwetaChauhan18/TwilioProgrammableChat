package com.example.shweta.twilioprogrammablechat.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import com.example.shweta.twilioprogrammablechat.R

class PermissionUtil {

    private val storagePermissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    /**
     * get storage permission
     * @return array
     */
    fun getStoragePermissions(): Array<String> {
        return storagePermissions
    }

    /**
     * verify permission
     * @param context
     * @param grantResults
     * @return Boolean
     */
    fun verifyPermissions(context: Context, grantResults: Array<String>): Boolean {
        for (result in grantResults) {
            if (ActivityCompat.checkSelfPermission(context, result) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * check marshmallow device permission
     * @return Boolean
     */
    fun checkMarshMellowPermission(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    companion object {

        const val INTENT_REQUEST_STORAGE_PERMISSION = 1001

        /**
         * after never ask for permission shoe dialog
         */
        fun showPermissionDialog(mContext: Context, msg: String) {
            val builder = AlertDialog.Builder(mContext, R.style.Dialog)
            builder.setTitle("Need Permission")
            builder.setMessage(msg)
            builder.setPositiveButton("YES") { dialogInterface, i ->
                dialogInterface.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", mContext.packageName, null)
                intent.data = uri
                mContext.startActivity(intent)
            }

            builder.setNegativeButton("NO") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()
        }
    }
}