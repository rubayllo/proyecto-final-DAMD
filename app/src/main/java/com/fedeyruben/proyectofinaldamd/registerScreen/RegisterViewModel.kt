package com.fedeyruben.proyectofinaldamd.registerScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    /********* Country *********/
    private val _country = MutableLiveData<String>()
    val country: LiveData<String> = _country

    /********* Phone *********/
    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    /********* Code Phone *********/
    private val _codePhone = MutableLiveData<Int>()
    val codePhone: LiveData<Int> = _codePhone


    fun onCountryChange(country: CountriesModel) {
        _country.value = country.country
        _codePhone.value = country.codePhone
    }

    fun onPhoneChange(phone: String) {
        if (phone.matches(Regex("^[0-9]*\$")) || phone.isEmpty()) {
            _phone.value = phone
        }
    }
}