package com.smart.browser.little.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs

/**
 * 提醒对话框.
 *
 * @author yushaojian
 * @date 2021-06-06 07:26
 */
class AlertDialogFragment : DialogFragment() {

    private val args by navArgs<AlertDialogFragmentArgs>()

    private var result = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val positive = args.positive ?: getString(android.R.string.ok)
        val negative = args.negative ?: getString(android.R.string.cancel)

        return AlertDialog.Builder(requireActivity())
            .setTitle(args.title)
            .setMessage(args.message)
            .setPositiveButton(positive) { _, _ -> result = true }
            .setNegativeButton(negative) { _, _ -> result = false }
            .create()
    }

    override fun onDestroyView() {
        setFragmentResult(ALERT_DIALOG_REQUEST, bundleOf(ALERT_DIALOG_RESULT to result))
        super.onDestroyView()
    }

    companion object {
        const val ALERT_DIALOG_REQUEST = "alert_dialog_request"
        const val ALERT_DIALOG_RESULT = "alert_dialog_result"
    }
}