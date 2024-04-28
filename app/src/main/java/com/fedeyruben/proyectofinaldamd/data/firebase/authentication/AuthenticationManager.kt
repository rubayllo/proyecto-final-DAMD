package com.fedeyruben.proyectofinaldamd.data.firebase.authentication
/*
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthenticationManager(private val context: Context) {

    private var storedVerificationId: String? = null

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("AuthenticationManager", "Verificación completada automáticamente.")
            // Implementar la lógica después de la verificación automática aquí
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("AuthenticationManager", "Error en la verificación: ${e.message}")
            // Implementar la lógica de manejo de errores aquí
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("AuthenticationManager", "Código enviado. ID de Verificación: $verificationId")
            storedVerificationId = verificationId
            // Implementar la lógica después de enviar el código aquí
        }
    }

    fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(code: String) {
        if (storedVerificationId == null) {
            Log.e("AuthenticationManager", "Intento de verificación sin ID. Código: $code")
            return
        }
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        // Aquí podrías implementar signInWithPhoneAuthCredential o pasar la credencial de vuelta al ViewModel
    }

    // Puedes mover signInWithPhoneAuthCredential aquí si es necesario
}
*/