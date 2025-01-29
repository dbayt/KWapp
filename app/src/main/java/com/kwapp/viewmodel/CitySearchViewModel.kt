//package com.kwapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.kwapp.retrofit.pojo.CityResponseWrapper
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import com.kwapp.retrofit.service.CityAutocompleteService
//import kotlinx.coroutines.launch
//
//class CitySearchViewModel : ViewModel() {
//    private val _citySuggestions = MutableStateFlow<List<String>>(emptyList())
//    val citySuggestions = _citySuggestions.asStateFlow()
//
//    private val apiService: CityAutocompleteService = Retrofit.Builder()
//        .baseUrl("https://your-api-url.com/") // Replace with your actual API URL
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//        .create(CityAutocompleteService::class.java)
//
//    fun fetchCitySuggestions(query: String, apiKey: String) {
//        if (query.length < 3) {
//            _citySuggestions.value = emptyList() // Avoid unnecessary API calls
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                val response: CityResponseWrapper = apiService.getCitySuggestions(query, 5, apiKey)
//
//                // ✅ Extract city names from the response safely
//                _citySuggestions.value = response.items?.map { it.name } ?: emptyList()
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _citySuggestions.value = emptyList()
//            }
//        }
//    }
//}