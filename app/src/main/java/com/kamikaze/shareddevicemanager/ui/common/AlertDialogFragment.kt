package com.kamikaze.shareddevicemanager.ui.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class AlertDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_MSG = "msg"
        private const val KEY_DATA = "data"

        fun newInstance(
            fragment: Fragment,
            msg: String,
            data: Bundle = Bundle()
        ): AlertDialogFragment {
            return AlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_MSG, msg)
                    putBundle(KEY_DATA, data)
                }
                setTargetFragment(fragment, 0)
            }
        }
    }

    interface AlertDialogListener {
        fun onClickListener(tag: String?, which: Int, data: Bundle)
    }

    var listener: AlertDialogListener? = null

    private val message: String by lazy {
        requireArguments().getString(KEY_MSG)!!
    }

    private val data: Bundle by lazy {
        requireArguments().getBundle(KEY_DATA)!!
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
        return AlertDialog.Builder(requireActivity())
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, id ->
                listener?.onClickListener(tag, id, data)
            }
            .setNegativeButton(android.R.string.cancel) { _, id ->
                listener?.onClickListener(tag, id, data)
            }
            .create()
    }
}