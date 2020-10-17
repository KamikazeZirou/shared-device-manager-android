package com.kamikaze.shareddevicemanager.ui.main.members

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kamikaze.shareddevicemanager.R
import kotlinx.android.synthetic.main.input_email.view.*

class InputEmailDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_TITLE = "title"

        fun newInstance(fragment: Fragment, title: String): InputEmailDialogFragment {
            return InputEmailDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                }
                setTargetFragment(fragment, 0)
            }
        }
    }

    interface InputEmailDialogListener {
        fun onClickListener(tag: String?, which: Int, email: String)
    }

    var listener: InputEmailDialogListener? = null

    private val title: String by lazy {
        requireArguments().getString(KEY_TITLE)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener =
            targetFragment as? InputEmailDialogListener ?: activity as? InputEmailDialogListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.input_email,
                null,
                false
            )

        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, id ->
                listener?.onClickListener(tag, id, view.input_email.text.toString())
            }
            .setNegativeButton(android.R.string.cancel) { _, id ->
                listener?.onClickListener(tag, id, "")
            }
            .create()
    }
}