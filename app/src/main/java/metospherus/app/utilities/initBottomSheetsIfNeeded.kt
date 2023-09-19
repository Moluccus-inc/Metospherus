package metospherus.app.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.countrypicker.config.CPDialogViewIds
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import metospherus.app.R
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
fun initBottomSheetsIfNeeded(
    context: Context,
    activity: FragmentActivity,
    auth: FirebaseAuth,
    db: FirebaseDatabase
) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        customView(R.layout.authentication_layout)
        cornerRadius(20f)
        cancelOnTouchOutside(false)
        setPeekHeight(Int.MIN_VALUE)

        lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
        lateinit var storedVerificationId: String

        val imgFlagHolder = view.findViewById<TextView>(R.id.imgFlagHolder)
        val countryName = view.findViewById<TextView>(R.id.countryName)
        val countryCodeName = view.findViewById<TextView>(R.id.countryCodeName)

        val phoneInputLayout = view.findViewById<LinearLayoutCompat>(R.id.phoneInputLayout)
        val codeInputLayout = view.findViewById<LinearLayoutCompat>(R.id.codeInputLayout)
        val otpView = view.findViewById<OtpTextView>(R.id.otp_view)
        val enterOTP = view.findViewById<TextView>(R.id.enterOTP)
        val createAccountConsent = view.findViewById<MaterialCheckBox>(R.id.createAccountConsent)
        val resendTokeOpt = view.findViewById<TextView>(R.id.resendTokeOpt)

        val phoneNumberButton = view.findViewById<MaterialButton>(R.id.phoneNumberButton)
        val completeVerification = view.findViewById<MaterialButton>(R.id.completeVerification)
        val phoneNumberInputs = view.findViewById<EditText>(R.id.phoneNumberInput)
        val countryCodePickerLayout = view.findViewById<MaterialCardView>(R.id.countryCodePickerLayout)

        phoneNumberButton.hide()
        createAccountConsent.isChecked = false
        createAccountConsent.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    phoneNumberButton.show()
                }

                false -> {
                    phoneNumberButton.hide()
                }
            }
        }

        phoneInputLayout.show()
        codeInputLayout.hide()

        val cpDialogViewIds = CPDialogViewIds(
            R.layout.custom_country_code_dialog,
            R.id.customDialogPicker,
            R.id.recyclerViewDialog,
            R.id.title_textview,
            R.id.search_view,
            null,
            null,
        )
        val initialPhCode = "+256"
        phoneNumberInputs.setText(initialPhCode)
        phoneNumberInputs.setSelection(initialPhCode.length)

        countryCodePickerLayout.setOnClickListener {
            windowContext.launchCountryPickerDialog(
                customMasterCountries = "UG,TZ,KE,TZ,RW,US,CA,DE",
                preferredCountryCodes = "UG,TZ,KE",
                dialogViewIds = cpDialogViewIds,
                secondaryTextGenerator = { country -> country.capitalEnglishName },
                showFullScreen = true,
                useCache = false
            ) { selectedCountry: CPCountry? ->
                imgFlagHolder.text = "${selectedCountry?.flagEmoji}"
                countryName.text = "${selectedCountry?.name}"
                countryCodeName.text = "+${selectedCountry?.phoneCode}"
                phoneNumberInputs.inputType = InputType.TYPE_CLASS_PHONE
                val initialPhoneCode = "+${selectedCountry?.phoneCode}"
                phoneNumberInputs.setText(initialPhoneCode)
                phoneNumberInputs.setSelection(initialPhoneCode.length)
                phoneNumberInputs.doAfterTextChanged {
                    val inputText = it.toString()
                    val countryCode = "+${selectedCountry?.phoneCode}"
                    if (inputText.startsWith(countryCode)) {
                        // The country code is intact, do nothing
                    } else {
                        phoneNumberInputs.setText(countryCode)
                        phoneNumberInputs.setSelection(countryCode.length)
                    }
                }
            }
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                dismiss()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        phoneInputLayout.show()
                        codeInputLayout.hide()
                        MoluccusToast(windowContext).showError("Invalid request")
                    }

                    is FirebaseTooManyRequestsException -> {
                        phoneInputLayout.show()
                        codeInputLayout.hide()
                        MoluccusToast(windowContext).showInformation("The SMS quota has been exceeded")
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        phoneInputLayout.show()
                        codeInputLayout.hide()
                        MoluccusToast(windowContext).showInformation("reCAPTCHA verification attempted with null Activity")
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                resendToken = token
                storedVerificationId = verificationId

                resendTokeOpt.setOnClickListener {
                    enterOTP.clearFocus()
                    phoneInputLayout.show()
                    codeInputLayout.hide()
                }
                enterOTP.text = "Enter the OTP code sent to ${
                    phoneNumberInputs.text?.trim().toString()
                        .replace(" ", "")
                } below to verify your phone number."

                phoneInputLayout.hide()
                codeInputLayout.show()

                otpView.requestFocusOTP()
                completeVerification.hide()
                otpView.otpListener = object : OTPListener {
                    override fun onInteractionListener() {}

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onOTPComplete(otp: String) {
                        completeVerification.show()
                        completeVerification.setOnClickListener {
                            when {
                                otp.isNotEmpty() -> {
                                    val credentialAuth: PhoneAuthCredential =
                                        PhoneAuthProvider.getCredential(
                                            verificationId,
                                            otp
                                        )

                                    auth.signInWithCredential(credentialAuth)
                                        .addOnCompleteListener(activity) { task ->
                                            if (task.isSuccessful) {
                                                val user = task.result?.user
                                                val createUserDatabase = mapOf(
                                                    "generalDescription" to mapOf(
                                                        "usrPreferedName" to "New Meto",
                                                        "usrPrimaryPhone" to user?.phoneNumber!!
                                                    ),
                                                    "generalDatabaseInformation" to mapOf(
                                                        "userUniqueIdentificationNumber" to user.uid,
                                                    ),
                                                )

                                                auth.currentUser?.let {
                                                    val participants =
                                                        db.getReference("participants")
                                                            .child(it.uid)
                                                    participants.addValueEventListener(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                dismiss()
                                                                activity.recreate()
                                                                MoluccusToast(windowContext).showSuccess(
                                                                    "Welcome Back To Metospherus A Comprehensive Medical System."
                                                                )
                                                            } else {
                                                                participants.setValue(
                                                                    createUserDatabase
                                                                )
                                                                    .addOnSuccessListener {
                                                                        dismiss()
                                                                        activity.recreate()
                                                                        MoluccusToast(
                                                                            windowContext
                                                                        ).showSuccess("Welcome To Metospherus A Comprehensive Medical System")
                                                                    }
                                                                    .addOnFailureListener { exception ->
                                                                        MoluccusToast(
                                                                            windowContext
                                                                        ).showError("Error !${exception.message}")
                                                                    }
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            TODO("Not yet implemented")
                                                        }

                                                    })
                                                }
                                            } else {
                                                //Log.w(TAG, "signInWithCredential:failure", task.exception)
                                                when (task.exception) {
                                                    is FirebaseAuthInvalidCredentialsException -> {
                                                        // The verification code entered was invalid
                                                    }
                                                }
                                            }
                                        }
                                }

                                else -> {
                                    otpView.showError()
                                    otpView.resetState()
                                }
                            }
                        }
                    }
                }
            }
        }

        phoneNumberButton.setOnClickListener {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(
                    phoneNumberInputs.text?.trim().toString().replace(" ", "")
                )
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            Toast.makeText(
                context,
                phoneNumberInputs.text?.trim().toString().replace(" ", ""),
                Toast.LENGTH_SHORT
            ).show()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}