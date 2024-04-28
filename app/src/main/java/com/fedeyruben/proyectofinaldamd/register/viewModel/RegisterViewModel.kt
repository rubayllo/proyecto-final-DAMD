package com.fedeyruben.proyectofinaldamd.register.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.navigation.AppScreensRoutes
import com.fedeyruben.proyectofinaldamd.register.registerScreen.CountriesModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class RegisterViewModel : ViewModel() {

    /********* País *********/
    private val _country = MutableLiveData<String>()
    val country: LiveData<String> = _country

    /********* Teléfono *********/
    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    /********* Código de Teléfono *********/
    private val _codePhone = MutableLiveData<String>()
    val codePhone: LiveData<String> = _codePhone

    /********* Habilitar Botón *********/
    private val _enableButton = MutableLiveData<Boolean>()
    val enableButton: LiveData<Boolean> = _enableButton

    /********* Código de Verificación *********/
    private val _verifyCode = MutableLiveData<String>()
    val verifyCode: LiveData<String> = _verifyCode

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    /********* Activar dialogo verificación *********/
    private val _dialogCodeOpen = MutableLiveData<Boolean>()
    val dialogCodeOpen: LiveData<Boolean> = _dialogCodeOpen


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

    fun onConfirmPhone(phoneNumber: String, phone: Boolean) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Esta devolución de llamada se invocará en dos situaciones:
                // 1 - Verificación instantánea. En algunos casos, el número de teléfono puede ser verificado instantáneamente
                //     sin necesidad de enviar o introducir un código de verificación.
                // 2 - Autoretirada. En algunos dispositivos, los servicios de Google Play pueden detectar automáticamente
                //     el SMS de verificación entrante y realizar la verificación sin acción del usuario.
                Log.d("PHONE1", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Esta devolución de llamada se invoca cuando se realiza una solicitud de verificación no válida,
                // por ejemplo, si el formato del número de teléfono no es válido.
                Log.w("PHONE1", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Solicitud no válida
                } else if (e is FirebaseTooManyRequestsException) {
                    // Se ha excedido la cuota de SMS para el proyecto
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // Intento de verificación de reCAPTCHA con Actividad nula
                }

                // Muestra un mensaje y actualiza la interfaz de usuario
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // El código de verificación SMS ha sido enviado al número de teléfono proporcionado, ahora
                // necesitamos pedir al usuario que ingrese el código y luego construir un credencial
                // combinando el código con un ID de verificación.

                Log.d("PHONE1", "onCodeSent:$verificationId  Token: ${token.hashCode()}")

                // Guarda el ID de verificación y el token de reenvío para poder usarlos más tarde
                storedVerificationId = verificationId
                resendToken = token
                Log.d("PHONE1", "ID de verificación: $storedVerificationId")

                _dialogCodeOpen.value = true
            }
        }

        if(phone) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Número de teléfono a verificar
                .setTimeout(60L, TimeUnit.SECONDS) // Tiempo de espera y unidad
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("PHONE1", "signInWithCredential:success")
                    val user = task.result?.user
                    Log.d("PHONE1", "User: $user")
                } else {
                    Log.w("PHONE1", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // El código de verificación ingresado no era válido
                    }
                }
            }
    }

    fun signInCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }




    fun onVerifyCodeChange(verifyCode: String) {
        if ( (verifyCode.matches(Regex("^[0-9]*\$")) || verifyCode.isEmpty()) && verifyCode.length <= 6) {
            _verifyCode.value = verifyCode
        }
        if (verifyCode.length == 6) {
            _enableButton.value = true
        }
    }

    fun dialogCodeOpen(visibility: Boolean) {
        _dialogCodeOpen.value = visibility
    }
}
