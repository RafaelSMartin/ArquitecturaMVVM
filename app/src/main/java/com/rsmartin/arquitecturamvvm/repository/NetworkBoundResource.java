package com.rsmartin.arquitecturamvvm.repository;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.rsmartin.arquitecturamvvm.AppExecutors;
import com.rsmartin.arquitecturamvvm.api.ApiResponse;

import java.util.Objects;

/***
 * Clase generica que nos proporciona un recurso de Room o Servicio.
 * Se encarga de decidir de donde coge el dato, memoria local o si no existe o
 * datos caducados los soliciatara.
 *
 * MediatorLiveData extiende de LiveData nos permite mergear varios objetos LiveData,
 * al cambiar nos lo notifica MediatorLiveData, es decir, que mergeamos todos los
 * LiveDatas en uno y solo tenemos uno que observar.
 *
 * Primero enseñamos los datos de Room y luego actualizamos si decidimos desde el Servidor.
 */

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutors appExecutors;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread //Esta etiqueta significa que solo puede ser llamado desde el Hilo principal
    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType data) {
                result.removeSource(dbSource);

                if(NetworkBoundResource.this.shouldFetchData(data)){
                    NetworkBoundResource.this.fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, (ResultType newData)->{
                        NetworkBoundResource.this.setValue(Resource.success(newData));
                    });
                }
            }
        });
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource){
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType newData) {
                NetworkBoundResource.this.setValue(Resource.loading(newData));
            }
        });
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if(response.isSuccessful()){
                appExecutors.diskIO().execute(new Runnable() { //Guardamos la info del Servicio en Room desde un hilo secundario
                    @Override
                    public void run() {
                        NetworkBoundResource.this.saveCallResult(NetworkBoundResource.this.processResponse(response));
                        appExecutors.mainThread().execute(()->{ // Mostramos los datos que hemos leido desde el Hilo principal
                            result.addSource(NetworkBoundResource.this.loadFromDb(), newData ->
                                    NetworkBoundResource.this.setValue(Resource.success(newData)));
                        });
                    }
                });
            } else { // Peticion ha fallado
                onFetchFailed();
                result.addSource(dbSource, newData ->
                        setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue){
        if(!Objects.equals(result.getValue(), newValue)){
            result.setValue(newValue);
        }
    }

    @MainThread
    protected abstract boolean shouldFetchData(ResultType data);

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    protected void onFetchFailed(){}

    public LiveData<Resource<ResultType>> asLiveData(){
        return result;
    }

    @WorkerThread //Solo puede ser en un Hilo de background
    protected RequestType processResponse(ApiResponse<RequestType> response){
        return response.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
