package com.example.shweta.twilioprogrammablechat.common

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity

/**
 * Created by Shweta on 30-09-2018.
 */
abstract class BaseActivityMVVM<B : ViewDataBinding> : AppCompatActivity() {
    private var mProgressDialog: Dialog? = null
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    protected fun setAndBindContentView(savedInstanceState: Bundle?, @LayoutRes layoutResID: Int) {
        binding = DataBindingUtil.setContentView(this, layoutResID)
        initialization()
    }

    /**
     *  Show Progress Dialog
     */
    fun showProgressDialog(mContext: Context) {
        if (mProgressDialog == null) {
            mProgressDialog = Utils.showProgress(mContext)
        }
        if (mProgressDialog != null)
            mProgressDialog?.show()

    }

    internal abstract fun initialization()

    /**
     *  Hide Progress Dialog
     */
    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog?.dismiss()
        }
    }

    override fun onStop() {
        hideProgressDialog()
        super.onStop()
    }
}