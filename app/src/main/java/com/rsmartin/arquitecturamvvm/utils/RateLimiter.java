package com.rsmartin.arquitecturamvvm.utils;

import android.os.SystemClock;
import android.util.ArrayMap;

import java.util.concurrent.TimeUnit;


/**
 * Nos permite decidir si podemos solicitar datos del webServices
 * o nos debemos quedar con los datos de Room.
 *
 * Dependemos de Key, si ha pasado más tiempo de ese solicitamos los datos de webservice.
 *
 * @param <KEY> clave de tiempo
 */
public class RateLimiter<KEY> {
    private ArrayMap<KEY, Long> timestamps = new ArrayMap<>();
    private final long timeout;

    public RateLimiter(int timeout, TimeUnit timeUnit){
        this.timeout = timeUnit.toMillis(timeout);
    }

    public synchronized boolean shouldFetch(KEY key){
        Long lastFetched = timestamps.get(key); //Guardamos por ultima vez cuando solicitamos datos
        long now = now();
        if(lastFetched == null){ //Si nunsca se habia solicitado se guarda
            timestamps.put(key, now);
            return true;
        }

        if(now - lastFetched > timeout){ //Si hemos sobrepasado el tiempo solicitamos el servicio
            timestamps.put(key, now);
            return true;
        }

        return false; // si no pasa por los otros return, no neceitamos solicitar al servicio y usamos Room
    }

    private long now() {
        return SystemClock.uptimeMillis();//El tiempo actual
    }

    public synchronized void reset(KEY key){
        timestamps.remove(key);
    }
}
