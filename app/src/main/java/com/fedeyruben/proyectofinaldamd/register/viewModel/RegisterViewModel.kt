package com.fedeyruben.proyectofinaldamd.register.viewModel

import android.app.Activity
import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.navigation.AppScreensRoutes
import com.fedeyruben.proyectofinaldamd.register.registerScreen.CountriesModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

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

    private var storedVerificationId: String? = null

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

    fun onRegister(navController: NavHostController, phone: Boolean, verify: Boolean) {
        if(phone) {
            navController.navigate(AppScreensRoutes.RegisterVerifyScreen.route)
        }
        if(verify) {
            navController.popBackStack()
            navController.navigate(AppScreensRoutes.HomeScreen.route)
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

    fun startAuthentication(){
       val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(phone.toString())
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
    }
    fun startPhoneNumberVerification(phoneNumber: String, context: Context) {

        Log.d("RegisterViewModel", "Iniciando verificación para el número: $phoneNumber")
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }



        // Callbacks para la verificación
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("RegisterViewModel", "Verificación completada automáticamente.")
            // Esta callback se invoca en dos situaciones:
            // 1. Verificación instantánea. En algunos casos, el número de teléfono puede ser verificado instantáneamente sin necesidad de enviar o ingresar un código de verificación.
            // 2. Auto-retrieval. En algunos dispositivos, Google Play services puede detectar automáticamente el código de verificación entrante y realizar la verificación sin la acción del usuario.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // Esta callback se invoca si ocurre un error durante la verificación.
            Log.e("RegisterViewModel", "Error en la verificación: ${e.message}")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // La SMS verification code ha sido enviada al número de teléfono proporcionado.
            // Guarda el ID de verificación y el token de reenvío para que puedas usarlos más tarde.
            Log.d("RegisterViewModel", "Código enviado. ID de Verificación: $verificationId")
            storedVerificationId = verificationId
        }
    }

    // Método para verificar el código ingresado por el usuario
    fun verifyPhoneNumberWithCode(code: String) {
        if (storedVerificationId == null) {
            Log.e("RegisterViewModel", "Intento de verificación sin ID. Código: $code")
            return
        }
        Log.d("RegisterViewModel", "Verificando código: $code con ID: $storedVerificationId")
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    // Método para iniciar sesión con la credencial
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Auth","OK")
                    _verifyCode.value = verifyCode.toString()
                    _enableButton.value = true
                } else {
                    Log.i("Auth","NO")
                }
            }
    }
}
