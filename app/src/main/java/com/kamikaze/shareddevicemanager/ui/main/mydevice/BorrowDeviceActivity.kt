package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.ActivityBorrowDeviceBinding
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.launch
import javax.inject.Inject


class BorrowDeviceActivity : DaggerAppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityBorrowDeviceBinding

    @Inject
    lateinit var viewModel: BorrowDeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_borrow_device)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.estimatedReturnDateEdit.setOnClickListener {
            val (year, month, day) = viewModel.getRawEstimatedReturnDate()
            val fragment = DatePickerDialogFragment.newInstance(year, month, day)
            fragment.show(supportFragmentManager, "datePicker")
        }

        binding.okButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.borrowDevice()
                finish()
            }
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {
        viewModel.setRawEstimatedReturnDate(year, month, day)
    }

    class DatePickerDialogFragment : DialogFragment() {
        companion object {
            fun newInstance(year: Int, month: Int, day: Int): DatePickerDialogFragment {
                val fragment = DatePickerDialogFragment()
                fragment.arguments = Bundle().apply {
                    putInt("year", year)
                    putInt("month", month)
                    putInt("day", day)
                }
                return fragment
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val args = arguments ?: throw IllegalStateException()

            val year = args.getInt("year")
            val month = args.getInt("month")
            val day = args.getInt("day")

            return DatePickerDialog(
                activity!!,
                activity as? DatePickerDialog.OnDateSetListener,
                year,
                month,
                day
            )
        }

    }
}
