package com.fedeyruben.proyectofinaldamd.register.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.navigation.AppScreensRoutes
import com.fedeyruben.proyectofinaldamd.register.registerScreen.CountriesModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class RegisterViewModel : ViewModel() {

    /********* Country *********/
    private val _country = MutableLiveData<String>()
    val country: LiveData<String> = _country

    /********* Phone *********/
    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    /********* Code Phone *********/
    private val _codePhone = MutableLiveData<String>()
    val codePhone: LiveData<String> = _codePhone

    /********* Enable Button *********/
    private val _enableButton = MutableLiveData<Boolean>()
    val enableButton: LiveData<Boolean> = _enableButton

    private val _verifyCode = MutableLiveData<String>()
    val verifyCode: LiveData<String> = _verifyCode


    fun onCountryChange(country: CountriesModel) {
        _country.value = country.country
        _codePhone.value = country.codePhone.toString()

        _enableButton.value = _phone.value != null && _country.value != null

    }

    fun onPhoneChange(phone: String) {
        if (phone.matches(Regex("^[0-9]*\$")) || phone.isEmpty()) {
            _phone.value = phone
        }
        _enableButton.value = phone.isNotEmpty() && _country.value != null
    }

    fun onRegister(
        navController: NavHostController,
        phone: Boolean,
        phoneNumber: String
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.d("PHONE1", "onVerificationCompleted")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("PHONE1", "onVerificationFailed")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Log.d("PHONE1", "onCodeSent")
                Log.d("PHONE1", "Code: ${p0}")
                Log.d("PHONE1", "Token: ${p1.hashCode()}")
            }
        }

        Log.d("PHONE1", "Phone number2: $phoneNumber")
        if(phone) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            navController.navigate(AppScreensRoutes.RegisterVerifyScreen.route)
        }

    }
    fun onVerifyCodeAuth(navController: NavHostController, verify: Boolean) {
        if(verify) {
            navController.popBackStack()
            navController.navigate(AppScreensRoutes.MapScreen.route)
        }
    }

    fun onVerifyCodeChange(verifyCode: String) {
        if ( (verifyCode.matches( Regex("^[0-9]*\$")) || verifyCode.isEmpty()) && verifyCode.length <= 6) {
            _verifyCode.value = verifyCode
        }
        if (verifyCode.length == 6) {
            _enableButton.value = true
        }
    }



}
