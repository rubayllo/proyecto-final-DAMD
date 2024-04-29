package com.fedeyruben.proyectofinaldamd.ui.registerScreen.registerScreen

class ListOfCountries {
    companion object {
        fun orderAlphabetically(): List<CountriesModel> {
            return countries.sortedBy { it.country }
        }

        private val countries = listOf<CountriesModel>(
            CountriesModel(country = "España", codePhone = 34),
            CountriesModel(country = "México", codePhone = 52),
            CountriesModel(country = "Argentina", codePhone = 54),
            CountriesModel(country = "Colombia", codePhone = 57),
            CountriesModel(country = "France", codePhone = 33),
            CountriesModel(country = "Germany", codePhone = 49),
            CountriesModel(country = "Italy", codePhone = 39),
            CountriesModel(country = "United States", codePhone = 1),
            CountriesModel(country = "United Kingdom", codePhone = 44),
            CountriesModel(country = "China", codePhone = 86),
            CountriesModel(country = "Japan", codePhone = 81),
            CountriesModel(country = "Russia", codePhone = 7),
            CountriesModel(country = "Brazil", codePhone = 55),
            CountriesModel(country = "India", codePhone = 91),
            CountriesModel(country = "Australia", codePhone = 61),
            CountriesModel(country = "South Africa", codePhone = 27),
            CountriesModel(country = "New Zealand", codePhone = 64),
        )
    }
}

data class CountriesModel(
    val country: String,
    val codePhone: Int
)