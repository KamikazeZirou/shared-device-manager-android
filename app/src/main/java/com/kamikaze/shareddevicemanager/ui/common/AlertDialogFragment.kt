package com.kamikaze.shareddevicemanager.ui.common

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class AlertDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_MSG = "msg"

        fun newInstance(fragment: Fragment, msg: String): AlertDialogFragment {
            return AlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MSG, msg)
                }
                setTargetFragment(fragment, 0)
            }
        }
    }

    interface AlertDialogListener {
        fun onClickListener(tag: String?, which: Int)
    }

    var listener: AlertDialogListener? = null
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = arguments!!.getString(KEY_MSG)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = targetFragment as? AlertDialogListener ?: activity as? AlertDialogListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    listener?.onClickListener(tag, id)
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    listener?.onClickListener(tag, id)
                })
            .create()
    }
}