package com.smart.browser.little.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.smart.browser.little.R
import com.art.maker.util.dp

/**
 * 提醒对话框.
 *
 * @author yushaojian
 * @date 2021-06-06 07:26
 */
class ExitWebAlertDialogFragment : DialogFragment() {

    private val args by navArgs<AlertDialogFragmentArgs>()

    private var result = false

    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val positive = args.positive ?: getString(android.R.string.ok)
        val negative = args.negative ?: getString(android.R.string.cancel)

        return AlertDialog.Builder(requireActivity())
            .setTitle(args.title)
            .setMessage(args.message)
            .setPositiveButton(positive) { _, _ -> result = true }
            .setNegativeButton(negative) { _, _ -> result = false }
            .create()
    }*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return NormalDialog(requireActivity(), 43.dp.toInt(), dimAmount = 0.6f)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_exit_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.tv_cancel).setOnClickListener{
            result = false
            dismiss()
        }

        view.findViewById<View>(R.id.tv_confirm).setOnClickListener{
            result = true
            dismiss()
        }

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