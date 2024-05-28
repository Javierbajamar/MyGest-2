package com.atecresa.preferencias;

public class Constantes {

    /*
     * ESTADO APP
     */
    public static final int app_serie_no_registrado = 0;
    public static final int app_serie_registrado = 1;
    public static final int app_demo_caducada = 2;  //SERIE REGISTRADO PERO DEMO CADUCADA

    /*
     * ERRORES
     */
    public static final int app_OK = 0;
    public static final int error_conexion = 1;
    public static final int error_licencia_demo = 2;
    public static final int error_licencia_caducada = 3;
    public static final int error_insertando_lineas = 4;
    public static final int error_tratando_JSON = 5;

    /*
     * DIFERENTES ARRAYLIST
     */
    public static final int lista_normal = 10;
    public static final int lista_funciones = 11;
    public static final int lista_busqueda = 12;

    //region TIPOS DE LINEA
    public static final int tipo_linea_nueva = 0;
    public static final int tipo_linea_recibida = 1;
    public static final int linea_nueva_menu_maestro = 2;
    public static final int linea_nueva_menu_detalle = 3;
    public static final int linea_recibida_menu_maestro = 4;
    public static final int linea_recibida_menu_detalle = 5;

    //endregion

    public static final int linea_estado_retenida = 100;
    public static final int linea_estado_pendiente_de_enviar = 101;

    /*
     * TIPOS DE MENSAJE RECIBIDOS POR EL SERVICIO
     */
    public static final int mensaje_actualizacion_mesas = 1;
    public static final int mensaje_nuevo_pedido_myorder = 2;
    public static final int mensaje_notificacion_nuevos_platos = 3;
    public static final int mensaje_actualizacion_cocina = 4;
    public static final int mensaje_actualizacion_acumulados = 5;
    public static final int mensaje_actualizacion_config = 6;
    /*
     * FUNCIONES COCINA
     */

    public static final int notificar_platos = 0;
    public static final int actualizar_platos = 2;
    public static final int enviar_nserie = 3;
    public static final int actualizar_acumulados = 4;

    /*
     * TIEMPO DE ESPERA MESA
     */
    public static final int tiempo_ok = 0;
    public static final int tiempo_con_retraso = 1;
    public static final int tiempo_con_retraso_grave = 2;

    /*
     * TIPO DE NOTIFICACI�N
     */

    public static final int notificacion_myorder = 0;
    public static final int notificacion_plato_preparado = 1;
    /*
     * VER MESAS EN COCINA
     */
    public static final int ver_mesas_en_curso = 0;
    public static final int ver_mesas_finalizadas = 1;
    public static final int ver_mesas_todas = 2;
    /*
     * Estados de lineas de comanda
     */
    //region LINEAS
    public static final int linea_cocina_recibida_hecha = 10;  //VERDE Y TACHADA
    public static final int linea_cocina_no_notificada_hecha = 11; //AHORA IRÁ CHECKEADA EN AZUL
    public static final int linea_cocina_recibida_sin_hacer = 12;
    public static final int linea_cocina_no_notificada_sin_hacer = 13;

    public static final int linea_cocina_en_preparacion = 14;
    //endregion
    /*
     * Constantes para identificar las diferentes activities
     */
    public static final int activity_busqueda_clientes = 0;
    public static final int activity_busqueda_articulos = 1;
    public static final int activity_vinculos = 2;

    //region PERMISOS
    public static final int permiso_leer_serial = 1;

    //endregion

    public static final int comando_voz = 10;

    //region REQUEST
    public static final int REQUEST_BUSCAR_ARTICULOS = 0;
    public static final int REQUEST_BUSCAR_CLIENTES = 10;
    public static final int REQUEST_VINCULOS = 20;
    public static final int REQUEST_COMANDO_VOZ = 30;
    public static final int REQUEST_COBRAR_MESA = 40;
    //endregion

    //region MODO DE USO APP
    public static final String MODO_COMANDERO = "0";
    public static final String MODO_COCINA = "1";
    public static final String MODO_CLOUD = "2";
    //endregion

    //region QR

    public static final int scan_empresa = 0;

    //endregion

    //region CIERRE DE CAJA
    public static final String imprimir_cierre_z = "Cierre de caja";
    public static final String imprimir_cierre_x = "Resumen de ventas";
    public static final String imprimir_ticket = "Ticket";

    //endregion


}
