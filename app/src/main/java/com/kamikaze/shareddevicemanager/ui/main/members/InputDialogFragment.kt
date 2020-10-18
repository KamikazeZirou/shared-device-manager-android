package com.kamikaze.shareddevicemanager.ui.main.members

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kamikaze.shareddevicemanager.R
import kotlinx.android.synthetic.main.input_dialog.view.*

class InputDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_LABEL = "label"
        private const val KEY_INPUT_TYPE = "input_type"

        fun newInstance(
            fragment: Fragment,
            title: String,
            label: String,
            inputType: Int = InputType.TYPE_CLASS_TEXT,
        ): InputDialogFragment {
            return InputDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_LABEL, label)
                    putInt(KEY_INPUT_TYPE, inputType)
                }
                setTargetFragment(fragment, 0)
            }
        }
    }

    interface InputDialogListener {
        fun onClickListener(tag: String?, which: Int, email: String)
    }

    var listener: InputDialogListener? = null

    private val title: String by lazy {
        requireArguments().getString(KEY_TITLE)!!
    }
    private val label: String by lazy {
        requireArguments().getString(KEY_LABEL)!!
    }
    private val inputType: Int by lazy {
        requireArguments().getInt(KEY_INPUT_TYPE)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener =
            targetFragment as? InputDialogListener ?: activity as? InputDialogListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.input_dialog,
                null,
                false
            )

        view.input_layout.hint = label

        val inputText = view.input_text.apply {
            inputType = (this@InputDialogFragment).inputType
        }

        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, id ->
                listener?.onClickListener(tag, id, inputText.text.toString())
            }
            .setNegativeButton(android.R.string.cancel) { _, id ->
                listener?.onClickListener(tag, id, "")
            }
            .create()
    }
}