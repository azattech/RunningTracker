package com.azat.runningtracker.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.azat.runningtracker.R
import com.azat.runningtracker.other.Constants.Companion.KEY_NAME
import com.azat.runningtracker.other.Constants.Companion.KEY_WEIGHT
import com.azat.runningtracker.other.Constants.Companion.POLICY_LINK
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 22/06/2020 - 10:31 PM *
 ************************/
@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()

        btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()
            if (success) {
                Snackbar.make(requireView(), "Saved changes", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(
                    requireView(),
                    "Please fill out all the fields",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        privacy_policy.setOnClickListener {
            /*  val action = NavGraphDirections.actionGlobalPrivacyPolicyFragment()
              findNavController().navigate(action) */

            val i = Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(POLICY_LINK))
            startActivity(i)
        }
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPref.getString(KEY_NAME, "")
        val weight = sharedPref.getFloat(KEY_WEIGHT, 80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go, $nameText!"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }
}