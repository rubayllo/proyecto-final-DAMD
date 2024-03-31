package com.fedeyruben.proyectofinaldamd.register.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.navigation.AppScreensRoutes
import com.fedeyruben.proyectofinaldamd.register.registerScreen.CountriesModel

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

    fun onRegister(navController: NavHostController, phone: Boolean, verify: Boolean) {
        if(phone) {
            navController.navigate(AppScreensRoutes.RegisterVerifyScreen.route)
        }
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
