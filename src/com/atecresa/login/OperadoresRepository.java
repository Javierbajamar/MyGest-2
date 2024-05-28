package com.atecresa.login;


import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;

/*
Clase que gestiona los operadores
 */
public class OperadoresRepository {

    private static List<Operador> lista_operadores;
    private static HashMap<String, Operador> mapa_operadores; //NO SÉ SI LO VOY A USAR
    private static MutableLiveData<List<Operador>> ITEMS;

    MutableLiveData<List<Operador>> getITEMS() {
        return ITEMS;
    }


}
