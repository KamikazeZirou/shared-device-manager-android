package com.kamikaze.shareddevicemanager.ui.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kamikaze.shareddevicemanager.R
import kotlinx.android.synthetic.main.input_dialog.view.input_layout
import kotlinx.android.synthetic.main.input_dialog.view.input_text

class InputDialogFragment : DialogFragment() {
    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_LABEL = "label"
        private const val KEY_INITIAL_VALUE = "initial_value"
        private const val KEY_INPUT_TYPE = "input_type"
        private const val KEY_DATA = "data"

        fun newInstance(
            fragment: Fragment,
            title: String,
            label: String,
            initialValue: String = "",
            inputType: Int = InputType.TYPE_CLASS_TEXT,
            data: Bundle = Bundle(),
        ): InputDialogFragment {
            return InputDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TITLE, title)
                    putString(KEY_LABEL, label)
                    putString(KEY_INITIAL_VALUE, initialValue)
                    putInt(KEY_INPUT_TYPE, inputType)
                    putBundle(KEY_DATA, data)
                }
                setTargetFragment(fragment, 0)
            }
        }
    }

    interface InputDialogListener {
        fun onClickListener(tag: String?, which: Int, value: String, data: Bundle)
    }

    var listener: InputDialogListener? = null

    private val title: String by lazy {
        requireArguments().getString(KEY_TITLE)!!
    }
    private val label: String by lazy {
        requireArguments().getString(KEY_LABEL)!!
    }
    private val initialValue: String by lazy {
        requireArguments().getString(KEY_INITIAL_VALUE, "")
    }
    private val inputType: Int by lazy {
        requireArguments().getInt(KEY_INPUT_TYPE)
    }
    private val data: Bundle by lazy {
        requireArguments().getBundle(KEY_DATA)!!
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
            setText(initialValue)
        }

        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, id ->
                listener?.onClickListener(tag, id, inputText.text.toString(), data)
            }
            .setNegativeButton(android.R.string.cancel) { _, id ->
                listener?.onClickListener(tag, id, "", data)
            }
            .create()
    }
}