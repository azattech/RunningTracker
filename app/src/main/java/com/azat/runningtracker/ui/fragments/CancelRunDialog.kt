package com.azat.runningtracker.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.azat.runningtracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 10/08/2020 - 8:12 PM  *
 ************************/
class CancelRunDialog : DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener: (() -> Unit)) {
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure that you want to cancel the current run and delete its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                yesListener?.let { yes ->
                    yes()
                }

            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }
}