package com.example.mypowerfulandroidapp.ui

data class DataState<T>(
    var error: Event<StateError>? = null,
    var loading: Loading = Loading(false),
    var data: Data<T>? = null
) {
    companion object {
        fun <T> error(
            response: Response
        ): DataState<T> {
            return DataState(
                error = Event(
                    StateError(
                        response
                    )
                )
            )
        }

        fun <T> loading(
            isLoading: Boolean,
            cashedData: T? = null
        ): DataState<T> {
            return DataState(
                loading = Loading(isLoading),
                data = Data(
                    data = Event.dataEvent(
                        cashedData
                    ),
                    response = null
                )
            )
        }

        fun <T> data(
            data: T? = null,
            response: Response? = null
        ): DataState<T> {
            return DataState(
                data = Data(
                    data = Event.dataEvent(data),
                    response = Event.responseEvent(response)
                )
            )
        }

    }

}
