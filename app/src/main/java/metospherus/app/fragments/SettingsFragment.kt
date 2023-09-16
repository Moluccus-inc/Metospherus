package metospherus.app.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.DynamicColors
import com.google.firebase.auth.FirebaseAuth
import metospherus.app.App
import metospherus.app.MainActivity
import metospherus.app.R
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())

        binding.backHolderKey.setOnClickListener {
            findNavController().navigate(R.id.action_to_home_from_settings)
        }

        val lightTheme = binding.lightTheme  // MaterialCard
        val darkTheme = binding.darkTheme  // MaterialCard
        val dynamicTheme = binding.dynamicTheme  // MaterialCard

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        lightTheme.isCheckable = true
        darkTheme.isCheckable = true
        dynamicTheme.isCheckable = true
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                lightTheme.isChecked = false
                darkTheme.isChecked = true
                dynamicTheme.isChecked = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                lightTheme.isChecked = true
                darkTheme.isChecked = false
                dynamicTheme.isChecked = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                lightTheme.isChecked = false
                darkTheme.isChecked = false
                dynamicTheme.isChecked = true
                DynamicColors.applyToActivitiesIfAvailable(App.instance)
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            activity?.recreate()
        }

        darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            activity?.recreate()
        }

        dynamicTheme.setOnClickListener {
            DynamicColors.applyToActivitiesIfAvailable(App.instance)
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            activity?.recreate()
        }

        binding.deleteAccountButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            performOfflineLogout()

            activity?.recreate()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun performOfflineLogout() {
        val userDao = appDatabase.profileLocal()
        val menstrualCycles = appDatabase.menstrualCycleLocal()
        val companionship = appDatabase.generalBrainResponse()
        userDao.deleteAllUserData()
        companionship.deleteAllUserData()
        menstrualCycles.deleteAllUserData()
        appDatabase.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}