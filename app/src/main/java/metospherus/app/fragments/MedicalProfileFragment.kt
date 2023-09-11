package metospherus.app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.database.profile_data.Profiles
import metospherus.app.databinding.FragmentMedicalProfileBinding
import metospherus.app.utilities.Constructor
import java.util.Locale

class MedicalProfileFragment : Fragment() {
    private var _binding: FragmentMedicalProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var appDatabase: AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicalProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        auth.useAppLanguage()
        db = FirebaseDatabase.getInstance()
        appDatabase = AppDatabase.getInstance(requireContext())

        binding.backHolderKey.setOnClickListener {
            findNavController().navigate(R.id.action_home_from_mdprofile)
        }
        binding.editProfile.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val userPatient = Constructor.getUserProfilesFromDatabase(appDatabase)
                userPatient?.let { profile ->
                    initProfileEditingIfNeeded(profile)
                }
            }
        }

        initialReturnProfileInformationIfNeeded()
    }

    private fun initProfileEditingIfNeeded(userPatient: Profiles) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.profileinstance_professionals_layout)
            cancelOnTouchOutside(false)
            cornerRadius(20f)

            val closeBottomSheetProfile = view.findViewById<ImageView>(R.id.CloseBottomSheetProfile)
            val fullLegalNameInput = view.findViewById<TextInputEditText>(R.id.fullLegalNameInput)
            val aboutDoctorInput = view.findViewById<TextInputEditText>(R.id.aboutDoctorInput)
            val doctorsEmailAddress = view.findViewById<TextInputEditText>(R.id.doctorsEmailAddress)
            val doctorsPhoneNumber = view.findViewById<TextInputEditText>(R.id.doctorsPhoneNumber)

            val medicalProfessionType =
                view.findViewById<AutoCompleteTextView>(R.id.medicalProfessionType)
            val medicalProfessionGender =
                view.findViewById<TextInputEditText>(R.id.medicalProfessionGender)
            val doctorsExperience = view.findViewById<TextInputEditText>(R.id.doctorsExperience)
            val doctorsLicense = view.findViewById<TextInputEditText>(R.id.doctorsLicense)
            val doctorsReference = view.findViewById<TextInputEditText>(R.id.doctorsReference)
            val doctorsReferenceHospitalEmail =
                view.findViewById<TextInputEditText>(R.id.doctorsReferenceHospitalEmail)

            fullLegalNameInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/usrPreferedName", inputs)
                    }
                }
            }
            aboutDoctorInput.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("medicalProfessionals/about", inputs)
                    }
                }
            }
            doctorsEmailAddress.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("generalDescription/usrPrimaryEmail", inputs)
                    }
                }
            }
            medicalProfessionGender.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("medicalProfessionals/mdGender", inputs)
                    }
                }
            }
            doctorsExperience.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("medicalProfessionals/mdExperience", inputs)
                    }
                }
            }
            doctorsLicense.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("medicalProfessionals/mdLicense", inputs)
                    }
                }
            }
            doctorsReference.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("medicalProfessionals/mdReference", inputs)
                    }
                }
            }
            doctorsReferenceHospitalEmail.doAfterTextChanged { p0 ->
                p0.let {
                    if (it?.isNotEmpty() == true) {
                        val inputs = it.toString().trim()
                        updateFirebaseDatabase("medicalProfessionals/mdReferenceEmail", inputs)
                    }
                }
            }

            val accountTypes = resources.getStringArray(R.array.medical_professionals)
            val adapter = ArrayAdapter(
                windowContext,
                android.R.layout.simple_dropdown_item_1line,
                accountTypes
            )
            medicalProfessionType.setAdapter(adapter)
            medicalProfessionType.setOnItemClickListener { parent, _, position, _ ->
                val selectedAccountType = parent.getItemAtPosition(position) as String
                updateFirebaseDatabase(
                    "medicalProfessionals/medicalProfessionType",
                    selectedAccountType
                )
            }

            closeBottomSheetProfile.setOnClickListener {
                dismiss()
            }

            fullLegalNameInput.setText(userPatient.generalDescription.usrPreferedName)
            doctorsEmailAddress.setText(userPatient.email)
            doctorsPhoneNumber.setText(userPatient.phoneNumber)
            aboutDoctorInput.setText(userPatient.medicalProfessionals.about)

            doctorsReference.setText(userPatient.medicalProfessionals.mdReference)
            doctorsLicense.setText(userPatient.medicalProfessionals.mdLicense)
            doctorsExperience.setText(userPatient.medicalProfessionals.mdExperience)
            medicalProfessionGender.setText(userPatient.medicalProfessionals.mdGender)
            doctorsReferenceHospitalEmail.setText(userPatient.medicalProfessionals.mdReferenceEmail)
            medicalProfessionType.setText(userPatient.medicalProfessionals.medicalProfessionType)
        }
    }

    private fun updateFirebaseDatabase(id: String, value: String) {
        if (value != "null" && value != "Not Set") {
            val uid = auth.uid
            val databaseRef = db.getReference("participants/$uid")
            databaseRef.child(id).setValue(value)
        }
    }

    private fun initialReturnProfileInformationIfNeeded() {
        CoroutineScope(Dispatchers.Main).launch {
            val userPatient = Constructor.getUserProfilesFromDatabase(appDatabase)
            userPatient?.let { patient ->
                binding.nameProfile.text =
                    patient.generalDescription.usrPreferedName ?: "Not Set"
                binding.handleProfile.text = patient.handle ?: "Not Set"
                binding.accountTypes.text =
                    patient.medicalProfessionals.medicalProfessionType ?: "Not Set"
                binding.aboutDoctorTv.text = patient.medicalProfessionals.about ?: "Not Set"

                Glide.with(requireContext())
                    .load(patient.avatar)
                    .placeholder(R.drawable.holder)
                    .into(binding.avatar)
            }

        }
    }
}